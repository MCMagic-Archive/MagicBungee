package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.ChatUtil;

public class Commandho extends MagicCommand {

    public Commandho() {
        super(Rank.DEVELOPER);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            String message = "";
            for (String arg : args) {
                message += arg + " ";
            }
            if (sender instanceof ProxiedPlayer) {
                Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
                for (Player tp : MagicBungee.getOnlinePlayers()) {
                    if (tp.getRank().getRankId() >= Rank.DEVELOPER.getRankId()) {
                        tp.sendMessage(ChatColor.RED + "[ADMIN CHAT] " + ChatColor.GRAY + player.getName() + ": " +
                                ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
                ChatUtil.logMessage(player.getUniqueId(), "/ho " + player.getName() + " " + message);
            }
            return;
        }
        sender.sendMessage(ChatColor.RED + "/ho [Message]");
    }
}
