package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.*;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.Ban;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Mute;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.BanUtil;
import us.mcmagic.magicbungee.utils.DateUtil;
import us.mcmagic.magicbungee.utils.MuteUtil;
import us.mcmagic.magicbungee.utils.SqlUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class Commandbseen extends MagicCommand {
    public TextComponent notFound = new TextComponent();
    private String divider = " - ";

    public Commandbseen() {
        super(Rank.CASTMEMBER);
        notFound.setText("That player can't be found!");
        notFound.setColor(ChatColor.RED);
        tabCompletePlayers = true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(final CommandSender sender, String label, final String[] args) {
        if (args.length == 1) {
            MagicBungee.getProxyServer().getScheduler().runAsync(MagicBungee.getInstance(), new Runnable() {
                        public void run() {
                            Player tp = MagicBungee.getPlayer(args[0]);
                            if (tp == null) {
                                String username = args[0];
                                UUID uuid;
                                TextComponent ls = new TextComponent();
                                TextComponent r = new TextComponent();
                                BaseComponent[] info;
                                TextComponent lastServer = new TextComponent();
                                try (Connection connection = SqlUtil.getConnection()) {
                                    PreparedStatement sql = connection.prepareStatement("SELECT * FROM player_data WHERE username=?");
                                    sql.setString(1, args[0]);
                                    ResultSet result = sql.executeQuery();
                                    if (!result.next()) {
                                        sender.sendMessage(notFound);
                                        result.close();
                                        sql.close();
                                        return;
                                    }
                                    username = result.getString("username");
                                    uuid = UUID.fromString(result.getString("uuid"));
                                    long lastSeen = result.getTimestamp("lastseen").getTime();
                                    Rank rank = Rank.fromString(result.getString("rank"));
                                    ls.setText(username + " has been away for " + DateUtil.formatDateDiff(lastSeen));
                                    ls.setColor(ChatColor.GREEN);
                                    r.setText("Rank: " + rank.getNameWithBrackets());
                                    r.setColor(ChatColor.RED);
                                    String ipAddress = result.getString("ipAddress");
                                    info = new ComponentBuilder(ipAddress).color(ChatColor.AQUA)
                                            .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ipseen " + ipAddress))
                                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                                    "Click to run an IP Search").color(ChatColor.AQUA).create()))
                                            .append(divider).color(ChatColor.DARK_GREEN)
                                            .append("Name Check").color(ChatColor.LIGHT_PURPLE)
                                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/namecheck " + username))
                                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                                    "Click to run a Name Check").color(ChatColor.AQUA).create()))
                                            .append(divider).color(ChatColor.DARK_GREEN)
                                            .append("Player Lookup").color(ChatColor.GREEN)
                                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                                    "Open Player Lookup Page").color(ChatColor.GREEN).create()))
                                            .event(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                                    "https://mcmagic.us/lookup/user/" + username))
                                            .create();
                                    lastServer.setText("Last Server: " + result.getString("server"));
                                    lastServer.setColor(ChatColor.YELLOW);
                                    result.close();
                                    sql.close();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    return;
                                }
                                Ban ban = BanUtil.getBan(uuid, username);
                                if (ban != null) {
                                    TextComponent banMsg = new TextComponent();
                                    if (!ban.isPermanent()) {
                                        banMsg.setText(username + " is Temporarily Banned for " + ban.getReason());
                                        if (ban.getReason().endsWith(" ")) {
                                            banMsg.addExtra("by " + ban.getSource() + ". Release: " + DateUtil.formatDateDiff(
                                                    ban.getRelease()));
                                        } else {
                                            banMsg.addExtra(" by " + ban.getSource() + ". Release: " + DateUtil.formatDateDiff(
                                                    ban.getRelease()));
                                        }
                                        banMsg.setColor(ChatColor.RED);
                                        sender.sendMessage(banMsg);
                                    } else {
                                        banMsg.setText(username + " is Banned for " + ban.getReason());
                                        if (ban.getReason().endsWith(" ")) {
                                            banMsg.addExtra("by " + ban.getSource() + ".");
                                        } else {
                                            banMsg.addExtra(" by " + ban.getSource() + ".");
                                        }
                                        banMsg.setColor(ChatColor.RED);
                                        sender.sendMessage(banMsg);
                                    }
                                }
                                Mute mute = MuteUtil.getMute(uuid, username);
                                if (mute != null) {
                                    if (mute.isMuted()) {
                                        if (mute.getRelease() < System.currentTimeMillis()) {
                                            MuteUtil.unmutePlayer(uuid);
                                            mute.setMuted(false);
                                        } else {
                                            TextComponent muteMsg = new TextComponent();
                                            muteMsg.setText(username + " is Muted for " + DateUtil.formatDateDiff(mute.
                                                    getRelease()) + " by " + mute.getSource() + ".");
                                            if (!mute.getReason().equals("")) {
                                                muteMsg.addExtra(" Reason: " + mute.getReason());
                                            }
                                            muteMsg.setColor(ChatColor.RED);
                                            sender.sendMessage(muteMsg);
                                        }
                                    }
                                }
                                sender.sendMessage(ls);
                                sender.sendMessage(r);
                                sender.sendMessage(info);
                                sender.sendMessage(lastServer);
                                return;
                            }
                            UUID uuid = tp.getUniqueId();
                            String suuid = uuid.toString();
                            Ban ban = BanUtil.getBan(uuid, tp.getName());
                            if (ban != null) {
                                TextComponent banMsg = new TextComponent();
                                if (!ban.isPermanent()) {
                                    banMsg.setText(tp.getName() + " is Temporarily Banned for " + ban.getReason());
                                    if (ban.getReason().endsWith(" ")) {
                                        banMsg.addExtra("by " + ban.getSource() + ". Release: " + DateUtil.formatDateDiff(
                                                ban.getRelease()));
                                    } else {
                                        banMsg.addExtra(" by " + ban.getSource() + ". Release: " + DateUtil.formatDateDiff(
                                                ban.getRelease()));
                                    }
                                    banMsg.setColor(ChatColor.RED);
                                    sender.sendMessage(banMsg);
                                } else {
                                    banMsg.setText(tp.getName() + " is Banned for " + ban.getReason());
                                    if (ban.getReason().endsWith(" ")) {
                                        banMsg.addExtra("by " + ban.getSource() + ".");
                                    } else {
                                        banMsg.addExtra(" by " + ban.getSource() + ".");
                                    }
                                    banMsg.setColor(ChatColor.RED);
                                    sender.sendMessage(banMsg);
                                }
                            } else {
                                if (tp.getMute().isMuted()) {
                                    if (tp.getMute().getRelease() < System.currentTimeMillis()) {
                                        MuteUtil.unmutePlayer(uuid);
                                        tp.getMute().setMuted(false);
                                    } else {
                                        Mute mute = tp.getMute();
                                        TextComponent muteMsg = new TextComponent();
                                        muteMsg.setText(tp.getName() + " is Muted for " + DateUtil.formatDateDiff(mute.getRelease()) + " by " + mute.getSource() + ".");
                                        if (!mute.getReason().equals("")) {
                                            muteMsg.addExtra(" Reason: " + mute.getReason());
                                        }
                                        muteMsg.setColor(ChatColor.RED);
                                        sender.sendMessage(muteMsg);
                                    }
                                }
                            }
                            long lastSeen = tp.getLoginTime();
                            TextComponent ls = new TextComponent();
                            TextComponent r = new TextComponent();
                            String s = tp.getServer() == null ? "Not Found" : tp.getServer().getInfo().getName();
                            BaseComponent[] lastServer = new ComponentBuilder("Current Server: ").color(ChatColor.YELLOW)
                                    .append(s).color(ChatColor.AQUA).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            new ComponentBuilder("Click to join this server!").color(ChatColor.GREEN).create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/server " + s)).create();
                            ls.setText(tp.getName() + " has been online for " + DateUtil.formatDateDiff(tp.getLoginTime()));
                            ls.setColor(ChatColor.GREEN);
                            r.setText("Rank: " + tp.getRank().getNameWithBrackets());
                            r.setColor(ChatColor.RED);
                            String ipAddress = tp.getAddress();
                            BaseComponent[] info = new ComponentBuilder(ipAddress).color(ChatColor.AQUA)
                                    .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ipseen " + ipAddress))
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                            "Click to run an IP Search").color(ChatColor.AQUA).create()))
                                    .append(divider).color(ChatColor.DARK_GREEN)
                                    .append("Name Check").color(ChatColor.LIGHT_PURPLE)
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/namecheck " + tp.getName()))
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                            "Click to run a Name Check").color(ChatColor.AQUA).create()))
                                    .append(divider).color(ChatColor.DARK_GREEN)
                                    .append("Player Lookup").color(ChatColor.GREEN)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                                            "Open Player Lookup Page").color(ChatColor.GREEN).create()))
                                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                            "https://mcmagic.us/lookup/user/" + tp.getName()))
                                    .create();
                            sender.sendMessage(ls);
                            sender.sendMessage(r);
                            sender.sendMessage(info);
                            sender.sendMessage(lastServer);
                        }
                    }
            );
            return;
        }
        sender.sendMessage(ChatColor.RED + "/bseen [Username]");
    }
}