package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.DateUtil;
import us.mcmagic.magicbungee.utils.SqlUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 2/4/16
 */
public class Commandmodlog extends MagicCommand {

    public Commandmodlog() {
        super(Rank.CASTMEMBER);
        tabCompletePlayers = true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        try {
            if (!(sender instanceof ProxiedPlayer)) {
                return;
            }
            Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
            if (args.length < 1 || args.length > 2) {
                player.sendMessage(new ComponentBuilder("/modlog [Username] [Bans/Mutes/Kicks]").color(ChatColor.RED).create());
                return;
            }
            String playername = args[0];
            Player tp = MagicBungee.getPlayer(playername);
            UUID uuid = null;
            if (tp == null) {
                String tuuid = SqlUtil.uuidFromUsername(playername);
                try {
                    uuid = UUID.fromString(tuuid);
                } catch (Exception e) {
                    player.sendMessage(new ComponentBuilder("Player not found!").color(ChatColor.RED).create());
                    return;
                }
            } else {
                uuid = tp.getUniqueId();
            }
            String action = "";
            if (args.length > 1) {
                action = args[1].toLowerCase();
            }
            switch (action) {
                case "bans": {
                    List<BaseComponent[]> msgs = new ArrayList<>();
                    try (Connection connection = SqlUtil.getConnection()) {
                        PreparedStatement sql = connection.prepareStatement("SELECT * FROM banned_players WHERE uuid=?");
                        sql.setString(1, uuid.toString());
                        ResultSet result = sql.executeQuery();
                        while (result.next()) {
                            ComponentBuilder comp = new ComponentBuilder("Reason: ").color(ChatColor.RED)
                                    .append(result.getString("reason").trim()).color(ChatColor.GREEN).append(" | ")
                                    .color(ChatColor.RED).append(result.getInt("permanent") == 1 ? "Permanent" : "Temporary")
                                    .color(ChatColor.GREEN).append(" | ").color(ChatColor.RED);
                            if (result.getInt("permanent") != 1) {
                                comp.append("Expires: ").color(ChatColor.RED).append(DateUtil.formatDateDiff(result
                                        .getTimestamp("release").getTime())).color(ChatColor.GREEN).append(" | ")
                                        .color(ChatColor.RED);
                            }
                            comp.append("Source: ").color(ChatColor.RED).append(result.getString("source"))
                                    .color(ChatColor.GREEN).append(" | Active: ").color(ChatColor.RED)
                                    .append(result.getInt("active") == 1 ? "True" : "False").color(ChatColor.GREEN);
                            msgs.add(comp.create());
                        }
                        result.close();
                        sql.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    player.sendMessage(new ComponentBuilder("Ban Log for " + playername + ":").color(ChatColor.GOLD).create());
                    if (msgs.isEmpty()) {
                        player.sendMessage(new ComponentBuilder("No bans!").color(ChatColor.GREEN).create());
                        return;
                    }
                    for (BaseComponent[] msg : msgs) {
                        player.sendMessage(msg);
                    }
                    return;
                }
                case "mutes": {
                    List<BaseComponent[]> msgs = new ArrayList<>();
                    try (Connection connection = SqlUtil.getConnection()) {
                        PreparedStatement sql = connection.prepareStatement("SELECT * FROM muted_players WHERE uuid=?");
                        sql.setString(1, uuid.toString());
                        ResultSet result = sql.executeQuery();
                        while (result.next()) {
                            boolean active = result.getInt("active") == 1;
                            ComponentBuilder comp = new ComponentBuilder("Reason: ").color(ChatColor.RED)
                                    .append(result.getString("reason").trim()).color(ChatColor.GREEN)
                                    .append(" | Source: ").color(ChatColor.RED).append(result.getString("source"))
                                    .color(ChatColor.GREEN);
                            if (active) {
                                comp.append(" | Expires: ").color(ChatColor.RED).append(DateUtil.formatDateDiff(result
                                        .getTimestamp("release").getTime())).color(ChatColor.GREEN);
                            }
                            comp.append(" | Active: ").color(ChatColor.RED).append(active ? "True" : "False")
                                    .color(ChatColor.GREEN);
                            msgs.add(comp.create());
                        }
                        result.close();
                        sql.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    player.sendMessage(new ComponentBuilder("Mute Log for " + playername + ":").color(ChatColor.GOLD).create());
                    if (msgs.isEmpty()) {
                        player.sendMessage(new ComponentBuilder("No mutes!").color(ChatColor.GREEN).create());
                        return;
                    }
                    for (BaseComponent[] msg : msgs) {
                        player.sendMessage(msg);
                    }
                    return;
                }
                case "kicks": {
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    List<BaseComponent[]> msgs = new ArrayList<>();
                    try (Connection connection = SqlUtil.getConnection()) {
                        PreparedStatement sql = connection.prepareStatement("SELECT * FROM kicks WHERE uuid=?");
                        sql.setString(1, uuid.toString());
                        ResultSet result = sql.executeQuery();
                        while (result.next()) {
                            ComponentBuilder comp = new ComponentBuilder("Reason: ").color(ChatColor.RED)
                                    .append(result.getString("reason").trim()).color(ChatColor.GREEN)
                                    .append(" | Source: ").color(ChatColor.RED).append(result.getString("source"))
                                    .color(ChatColor.GREEN).append(" | Time: ").color(ChatColor.RED)
                                    .append(df.format(result.getTimestamp("time"))).color(ChatColor.GREEN);
                            msgs.add(comp.create());
                        }
                        result.close();
                        sql.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    player.sendMessage(new ComponentBuilder("Kick Log for " + playername + ":").color(ChatColor.GOLD).create());
                    if (msgs.isEmpty()) {
                        player.sendMessage(new ComponentBuilder("No kicks!").color(ChatColor.GREEN).create());
                        return;
                    }
                    for (BaseComponent[] msg : msgs) {
                        player.sendMessage(msg);
                    }
                    return;
                }
            }
            if (args.length == 1) {
                int banCount = 0;
                int muteCount = 0;
                int kickCount = 0;
                try (Connection connection = SqlUtil.getConnection()) {
                    PreparedStatement bans = connection.prepareStatement("SELECT count(*) FROM banned_players WHERE uuid=?");
                    bans.setString(1, uuid.toString());
                    ResultSet bansresult = bans.executeQuery();
                    bansresult.next();
                    banCount = bansresult.getInt("count(*)");
                    bansresult.close();
                    bans.close();
                    PreparedStatement mutes = connection.prepareStatement("SELECT count(*) FROM muted_players WHERE uuid=?");
                    mutes.setString(1, uuid.toString());
                    ResultSet mutesresult = mutes.executeQuery();
                    mutesresult.next();
                    muteCount = mutesresult.getInt("count(*)");
                    mutesresult.close();
                    mutes.close();
                    PreparedStatement kicks = connection.prepareStatement("SELECT count(*) FROM kicks WHERE uuid=?");
                    kicks.setString(1, uuid.toString());
                    ResultSet kicksresult = kicks.executeQuery();
                    kicksresult.next();
                    kickCount = kicksresult.getInt("count(*)");
                    kicksresult.close();
                    kicks.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                player.sendMessage(new ComponentBuilder("Moderation Log for " + playername + ": ").color(ChatColor.GREEN)
                        .append(banCount + " Bans, " + muteCount + " Mutes, " + kickCount + " Kicks")
                        .color(ChatColor.YELLOW).create());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}