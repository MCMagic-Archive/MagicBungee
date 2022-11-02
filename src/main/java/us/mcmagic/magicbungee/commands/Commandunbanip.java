package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.BanUtil;

public class Commandunbanip extends MagicCommand {

    public Commandunbanip() {
        super(Rank.CASTMEMBER);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            if (args.length < 1) {
                sender.sendMessage(new ComponentBuilder("/unbanip [IP_Address]").color(ChatColor.RED).create());
                return;
            }
            String ip = args[0];
            if (!BanUtil.isBannedIP(ip)) {
                sender.sendMessage(new ComponentBuilder("That IP is not banned!").color(ChatColor.RED).create());
                return;
            } else {
                BanUtil.unbanIP(ip);
                MagicBungee.dashboard.announceUnban("IP " + ip, "Console");
            }
            return;
        }
        Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/unbanip [IP_Address]");
            return;
        }
        String ip = args[0];
        if (!BanUtil.isBannedIP(ip)) {
            player.sendMessage(ChatColor.RED + "That IP is not banned!");
        } else {
            BanUtil.unbanIP(ip);
            MagicBungee.dashboard.announceUnban("IP " + ip, player.getName());
        }
    }
}