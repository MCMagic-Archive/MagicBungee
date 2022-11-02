package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.listeners.PlayerChat;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.ChatUtil;

import java.util.Collections;

public class Commandmutechat extends MagicCommand {

    public Commandmutechat() {
        super(Rank.CASTMEMBER);
        aliases = Collections.singletonList("chatmute");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return;
        }
        Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        final String pserver = player.getServer().getInfo().getName();
        if (ChatUtil.isParkChat(pserver)) {
            if (PlayerChat.mutedServers.containsKey("ParkChat")) {
                String msg = ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "MCMagic Chat" + ChatColor.WHITE + "] " +
                        ChatColor.YELLOW + "Chat has been unmuted";
                String msgname = ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "MCMagic Chat" + ChatColor.WHITE + "] " +
                        ChatColor.YELLOW + "Chat has been unmuted by " + player.getName();
                PlayerChat.mutedServers.remove("ParkChat");
                for (Player tp : MagicBungee.getOnlinePlayers()) {
                    String server;
                    try {
                        server = tp.getServer().getInfo().getName();
                    } catch (Exception ignored) {
                        continue;
                    }
                    if (server != null) {
                        if (ChatUtil.isParkChat(tp.getServer().getInfo().getName())) {
                            if (tp.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                                tp.sendMessage(msg);
                            } else {
                                tp.sendMessage(msgname);
                            }
                        }
                    }
                }
                return;
            }
            String msg = ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "MCMagic Chat" + ChatColor.WHITE + "] " +
                    ChatColor.YELLOW + "Chat has been muted";
            String msgname = ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "MCMagic Chat" + ChatColor.WHITE + "] " +
                    ChatColor.YELLOW + "Chat has been muted by " + player.getName();
            PlayerChat.mutedServers.put("ParkChat", true);
            for (Player tp : MagicBungee.getOnlinePlayers()) {
                String server;
                try {
                    server = tp.getServer().getInfo().getName();
                } catch (Exception ignored) {
                    continue;
                }
                if (server != null) {
                    if (ChatUtil.isParkChat(tp.getServer().getInfo().getName())) {
                        if (tp.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                            tp.sendMessage(msg);
                        } else {
                            tp.sendMessage(msgname);
                        }
                    }
                }
            }
            return;
        }
        if (PlayerChat.mutedServers.containsKey(pserver)) {
            String msg = ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "MCMagic Chat" + ChatColor.WHITE + "] " +
                    ChatColor.YELLOW + "Chat has been unmuted";
            String msgname = ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "MCMagic Chat" + ChatColor.WHITE + "] " +
                    ChatColor.YELLOW + "Chat has been unmuted by " + player.getName();
            PlayerChat.mutedServers.remove(pserver);
            for (Player tp : MagicBungee.getOnlinePlayers()) {
                String server;
                try {
                    server = tp.getServer().getInfo().getName();
                } catch (Exception ignored) {
                    continue;
                }
                if (server != null) {
                    if (server.equals(pserver)) {
                        if (tp.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                            tp.sendMessage(msg);
                        } else {
                            tp.sendMessage(msgname);
                        }
                    }
                }
            }
            return;
        }
        String msg = ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "MCMagic Chat" + ChatColor.WHITE + "] " +
                ChatColor.YELLOW + "Chat has been muted";
        String msgname = ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "MCMagic Chat" + ChatColor.WHITE + "] " +
                ChatColor.YELLOW + "Chat has been muted by " + player.getName();
        PlayerChat.mutedServers.put(pserver, true);
        for (Player tp : MagicBungee.getOnlinePlayers()) {
            String server;
            try {
                server = tp.getServer().getInfo().getName();
            } catch (Exception ignored) {
                continue;
            }
            if (server != null) {
                if (server.equals(pserver)) {
                    if (tp.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                        tp.sendMessage(msg);
                    } else {
                        tp.sendMessage(msgname);
                    }
                }
            }
        }
    }
}