package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.permissions.Rank;

public class Commandb extends MagicCommand {

    public Commandb() {
        super(Rank.CASTMEMBER);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            String message = "";
            for (String arg : args) {
                message += arg + " ";
            }
            String sname = sender.getName();
            if (!(sender instanceof ProxiedPlayer)) {
                sname = "Server";
            }
            BaseComponent[] msg = TextComponent.fromLegacyText(ChatColor.WHITE + "[" + ChatColor.AQUA + "Information" +
                    ChatColor.WHITE + "] " + ChatColor.GREEN + ChatColor.translateAlternateColorCodes('&', message));
            BaseComponent[] staff = TextComponent.fromLegacyText(ChatColor.WHITE + "[" + ChatColor.AQUA +
                    sname + ChatColor.WHITE + "] " + ChatColor.GREEN +
                    ChatColor.translateAlternateColorCodes('&', message));
            for (ProxiedPlayer tp : MagicBungee.getProxyServer().getPlayers()) {
                if (MagicBungee.getPlayer(tp.getUniqueId()).getRank().getRankId() >= Rank.CASTMEMBER.getRankId()) {
                    tp.sendMessage(staff);
                } else {
                    tp.sendMessage(msg);
                }
            }
            return;
        }
        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "/b [Message]"));
    }
}