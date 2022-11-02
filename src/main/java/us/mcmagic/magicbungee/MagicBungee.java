package us.mcmagic.magicbungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import us.mcmagic.magicbungee.dashboard.Dashboard;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.listeners.*;
import us.mcmagic.magicbungee.permissions.PermManager;
import us.mcmagic.magicbungee.socket.SocketConnection;
import us.mcmagic.magicbungee.threads.BroadcastClock;
import us.mcmagic.magicbungee.utils.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MagicBungee extends Plugin {
    private static HashMap<UUID, Player> onlinePlayers = new HashMap<>();
    public static List<String> swearList = new ArrayList<>();
    public static List<String> swearListSpecific = new ArrayList<>();
    public static String motd;
    public static String motdmaintenance;
    public static boolean maintenance;
    public static List<String> info;
    public static long enableTime;
    private static ProxyServer server;
    private static HashMap<UUID, String> userCache = new HashMap<>();
    public static boolean canJoin = false;
    private static MagicBungee instance;
    public static AFKUtil afkUtil;
    public static PermManager permManager;
    public static SocketConnection socketConnection;
    public static StatUtil statUtil;
    public static PartyUtil partyUtil;
    public static CommandUtil commandUtil;
    public static Dashboard dashboard;
    private PlayerJoinAndLeave playerJoinAndLeave;

    @Override
    public void onEnable() {
        enableTime = System.currentTimeMillis();
        instance = this;
        server = getProxy();
        BroadcastClock.initialize();
        SqlUtil.initialize();
        File file = new File("plugins/MagicBungee/config.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Configuration config = ConfigurationProvider.getProvider(
                    YamlConfiguration.class).load(file);
            List<String> list = config.getStringList("swear-list.list");
            List<String> slist = config.getStringList("swear-list.specific");
            for (String item : list) {
                swearList.add(item);
            }
            for (String item : slist) {
                swearListSpecific.add(item);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        File motd = new File("plugins/MagicBungee/motd.yml");
        if (!motd.exists()) {
            try {
                motd.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Configuration motdfile = ConfigurationProvider.getProvider(YamlConfiguration.class).load(motd);
            MagicBungee.motd = motdfile.getString("motd");
            MagicBungee.motdmaintenance = motdfile.getString("motd-maintenance");
            MagicBungee.info = motdfile.getStringList("infolist");
            MagicBungee.maintenance = motdfile.getBoolean("maintenance-mode");
        } catch (IOException e) {
            e.printStackTrace();
        }
        File servers = new File("plugins/MagicBungee/servers.yml");
        if (!servers.exists()) {
            try {
                servers.createNewFile();
                Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(servers);
                List<String> slist = new ArrayList<>();
                slist.add("TTC");
                slist.add("MK");
                slist.add("Epcot");
                slist.add("HWS");
                slist.add("AK");
                slist.add("Creative");
                slist.add("Arcade");
                config.set("servers", slist);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dashboard = new Dashboard();
        afkUtil = new AFKUtil();
        permManager = new PermManager();
        socketConnection = new SocketConnection();
        statUtil = new StatUtil();
        partyUtil = new PartyUtil();
        commandUtil = new CommandUtil();
        registerThings();
        getProxyServer().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                MagicBungee.canJoin = true;
                System.out.println("Players can now join");
            }
        }, 2, TimeUnit.SECONDS);
        getProxyServer().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                for (Player tp : getOnlinePlayers()) {
                    if (getProxyServer().getPlayer(tp.getUniqueId()) == null) {
                        playerJoinAndLeave.logout(tp);
                    }
                }
            }
        }, 0L, 1L, TimeUnit.MINUTES);
    }

    @Override
    public void onDisable() {
        socketConnection.stop();
        for (ProxiedPlayer player : getProxyServer().getPlayers()) {
            player.disconnect(new ComponentBuilder("We're restarting our servers, check back in a few moments!")
                    .color(ChatColor.AQUA).create());
        }
    }

    public static MagicBungee getInstance() {
        return instance;
    }

    public static ProxyServer getProxyServer() {
        return server;
    }

    public HashMap<UUID, String> getUserCache() {
        return new HashMap<>(userCache);
    }

    public void addToUserCache(UUID uuid, String name) {
        userCache.put(uuid, name);
    }

    public void removeFromUserCache(UUID uuid) {
        userCache.remove(uuid);
    }

    public static void sendMessageDelayed(int delay, final UUID uuid, final TextComponent message) {
        MagicBungee.getProxyServer().getScheduler().schedule(MagicBungee.getInstance(), new Runnable() {
            public void run() {
                try {
                    MagicBungee.getProxyServer().getPlayer(uuid).sendMessage(message);
                } catch (Exception ignored) {
                }
            }
        }, delay, TimeUnit.SECONDS);
    }

    public static Player createPlayer(UUID uuid, String name, String ipAddress) {
        try {
            Player player = SqlUtil.createPlayer(uuid, name, ipAddress);
            onlinePlayers.put(uuid, player);
            return player;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Player getPlayer(String name) {
        for (Player player : getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null;
    }

    public static Player getPlayer(UUID uuid) {
        return onlinePlayers.get(uuid);
    }

    public static void playerLogout(Player player) {
        try {
            SqlUtil.logout(player);
        } catch (Exception ignored) {
        }
        PlayerChat.playerLogout(player.getUniqueId());
        removePlayer(player.getUniqueId());
    }

    public static List<Player> getOnlinePlayers() {
        List<Player> list = new ArrayList<>();
        for (Map.Entry<UUID, Player> entry : onlinePlayers.entrySet()) {
            if (entry.getValue() == null) {
                onlinePlayers.remove(entry.getKey());
            } else {
                list.add(entry.getValue());
            }
        }
        return list;
    }

    public static void removePlayer(UUID uuid) {
        onlinePlayers.remove(uuid);
    }

    public static Configuration config() {
        try {
            File file = new File("plugins/MagicBungee/motd.yml");
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void registerThings() {
        PluginManager pm = ProxyServer.getInstance().getPluginManager();
        pm.registerListener(this, new ChatUtil());
        pm.registerListener(this, new MagicBandUtil());
        pm.registerListener(this, permManager);
        pm.registerListener(this, statUtil);
        pm.registerListener(this, new ProxyPing());
        pm.registerListener(this, new TabComplete());
        pm.registerListener(this, new PlayerChat());
        this.playerJoinAndLeave = new PlayerJoinAndLeave();
        pm.registerListener(this, playerJoinAndLeave);
        pm.registerListener(this, new ResourceUtil());
        pm.registerListener(this, new ServerConnect());
        pm.registerListener(this, new ServerKick());
        pm.registerListener(this, new ServerSwitch());
        pm.registerListener(this, new WarpUtil());
    }
}