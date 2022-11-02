package us.mcmagic.magicbungee.utils;

import com.vexsoftware.votifier.bungee.events.VotifierEvent;
import com.vexsoftware.votifier.model.Vote;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.commands.Commandapply;
import us.mcmagic.magicbungee.handlers.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Marc on 6/20/15
 */
public class StatUtil implements Listener {
    private int playerCount = 0;
    private KeyPair keyPair;
    public HashMap<String, String> servers = new HashMap<>();
    private HashMap<String, Integer> voteServices = new HashMap<>();

    public StatUtil() {
        initialize();
    }

    public void initialize() {
        reload();
        File file = new File("plugins/MagicBungee/config.yml");
        try {
            Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            if (config.getBoolean("test-network")) {
                MagicBungee.getProxyServer().getLogger().warning("test-network enabled, disabling stat collection!");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            MagicBungee.getProxyServer().getLogger().warning("No test-network value detected, disabling stat collection!");
            return;
        }
        try {
            MagicBungee.getProxyServer().getScheduler().schedule(MagicBungee.getInstance(), new Runnable() {
                @Override
                public void run() {
                    int count = MagicBungee.getProxyServer().getOnlineCount();
                    for (ServerInfo server : MagicBungee.getProxyServer().getServers().values()) {
                        if (server.getPlayers().size() == 0) {
                            continue;
                        }
                        try {
                            ByteArrayOutputStream b = new ByteArrayOutputStream();
                            DataOutputStream out = new DataOutputStream(b);
                            out.writeUTF("OnlineCount");
                            out.writeInt(count);
                            server.sendData("BungeeCord", b.toByteArray());
                        } catch (IOException ignored) {
                        }
                    }
                }
            }, 0L, 10L, TimeUnit.SECONDS);
            MagicBungee.getProxyServer().getScheduler().schedule(MagicBungee.getInstance(), new Runnable() {
                @Override
                public void run() {
                    int count = MagicBungee.getProxyServer().getOnlineCount();
                    if (count != playerCount) {
                        playerCount = count;
                    }
                    setValue("count", playerCount);
                }
            }, 0L, 1L, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onVotifier(VotifierEvent event) {
        Vote vote = event.getVote();
        String username = vote.getUsername();
        Player player = MagicBungee.getPlayer(username);
        final UUID uuid;
        if (player == null) {
            String temp = SqlUtil.uuidFromUsername(username);
            if (temp.equals("")) {
                return;
            } else {
                uuid = UUID.fromString(temp);
            }
        } else {
            uuid = player.getUniqueId();
            player.sendMessage(new ComponentBuilder("Thanks for supporting our server! You received ")
                    .color(ChatColor.GREEN).append("5 Tokens").color(ChatColor.GOLD).bold(true)
                    .append(" for voting for us!", ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN).create());
        }
        int id = 0;
        String service = vote.getServiceName().toLowerCase();
        for (Map.Entry<String, Integer> entry : new HashSet<>(voteServices.entrySet())) {
            if (entry.getKey().trim().equalsIgnoreCase(service.trim())) {
                id = entry.getValue();
                break;
            }
        }
        final int finalId = id;
        MagicBungee.getProxyServer().getScheduler().runAsync(MagicBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                vote(uuid, finalId);
            }
        });
    }

    private void setValue(String type, int value) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO stats (time, type, value) VALUES ('" +
                    (System.currentTimeMillis() / 1000) + "','" + type + "','" + value + "')");
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void vote(UUID uuid, int serverId) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement q = connection.prepareStatement("SELECT vote FROM player_data WHERE uuid=?");
            q.setString(1, uuid.toString());
            ResultSet qres = q.executeQuery();
            if (!qres.next()) {
                return;
            }
            boolean cancel = false;
            if (System.currentTimeMillis() - qres.getLong("vote") <= 43200000) {
                cancel = true;
            }
            qres.close();
            q.close();
            if (cancel) {
                ProxiedPlayer tp = MagicBungee.getProxyServer().getPlayer(uuid);
                if (tp != null) {
                    tp.sendMessage(new ComponentBuilder("You already claimed a reward for voting in the past 12 hours!")
                            .color(ChatColor.RED).create());
                }
                return;
            }
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET tokens=tokens+5,vote=?," +
                    "lastvote=? WHERE uuid=?");
            sql.setLong(1, System.currentTimeMillis());
            sql.setInt(2, serverId);
            sql.setString(3, uuid.toString());
            sql.execute();
            sql.close();
            PreparedStatement log = connection.prepareStatement("INSERT INTO economy_logs (uuid, amount, type, source," +
                    " server, timestamp) VALUES ('" + uuid.toString() + "', '5', 'add tokens', 'Vote', " +
                    "'BungeeCord', '" + System.currentTimeMillis() / 1000L + "')");
            log.execute();
            log.close();
            ProxiedPlayer p = MagicBungee.getProxyServer().getPlayer(uuid);
            if (p != null) {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                out.writeUTF("MagicVote");
                out.writeUTF(uuid.toString());
                p.getServer().sendData("BungeeCord", b.toByteArray());
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        voteServices.clear();
        try (Connection connection = Commandapply.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT siteid,name FROM vote");
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                voteServices.put(result.getString("name").toLowerCase(), result.getInt("siteid"));
            }
            result.close();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}