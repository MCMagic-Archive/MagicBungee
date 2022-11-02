package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.AddressBan;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.BanUtil;

public class Commandbanip extends MagicCommand {

    public Commandbanip() {
        super(Rank.CASTMEMBER);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            if (args.length < 2) {
                sender.sendMessage(new ComponentBuilder("/banip [IP Address] [Reason]").color(ChatColor.RED).create());
                return;
            }
            String ip = args[0];
            String r = "";
            for (int i = 1; i < args.length; i++) {
                r += args[i] + " ";
            }
            String reason = r.substring(0, 1).toUpperCase() + r.substring(1);
            reason = reason.trim();
            BanUtil.banIP(ip, reason, "Console");
            if (!ip.contains("*")) {
                for (Player tp : MagicBungee.getOnlinePlayers()) {
                    if (tp.getAddress().equals(ip)) {
                        try {
                            tp.kickPlayer(ChatColor.RED + "Your IP Has Been Banned For " + ChatColor.AQUA + reason);
                        } catch (Exception ignored) {
                        }
                    }
                }
            } else {
                for (Player tp : MagicBungee.getOnlinePlayers()) {
                    String[] list = tp.getAddress().split("\\.");
                    String range = list[0] + "." + list[1] + "." + list[2] + ".*";
                    if (range.equalsIgnoreCase(ip)) {
                        try {
                            tp.kickPlayer(ChatColor.RED + "Your IP Range Has Been Banned For " + ChatColor.AQUA + reason);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            MagicBungee.dashboard.announceBan(new AddressBan(ip, reason, "Console"));
            return;
        }
        Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/banip [IP Address] [Reason]");
            return;
        }
        String ip = args[0];
        String r = "";
        for (int i = 1; i < args.length; i++) {
            r += args[i] + " ";
        }
        String reason = r.substring(0, 1).toUpperCase() + r.substring(1);
        reason = reason.trim();
        BanUtil.banIP(ip, reason, player.getName());
        if (!ip.contains("*")) {
            for (Player tp : MagicBungee.getOnlinePlayers()) {
                if (tp.getAddress().equals(ip)) {
                    try {
                        tp.kickPlayer(ChatColor.RED + "Your IP Has Been Banned For " + ChatColor.AQUA + reason);
                    } catch (Exception ignored) {
                    }
                }
            }
        } else {
            for (Player tp : MagicBungee.getOnlinePlayers()) {
                String[] list = tp.getAddress().split("\\.");
                String range = list[0] + "." + list[1] + "." + list[2] + ".*";
                if (range.equalsIgnoreCase(ip)) {
                    try {
                        tp.kickPlayer(ChatColor.RED + "Your IP Range Has Been Banned For " + ChatColor.AQUA + reason);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        MagicBungee.dashboard.announceBan(new AddressBan(ip, reason, player.getName()));
    }
}