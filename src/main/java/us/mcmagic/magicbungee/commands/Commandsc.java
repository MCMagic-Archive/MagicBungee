package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.ChatUtil;

/**
 * Created by Marc on 1/14/15
 */
public class Commandsc extends MagicCommand {

    public Commandsc() {
        super(Rank.EARNINGMYEARS);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length > 0) {
            String message = "";
            for (String arg : args) {
                message += arg + " ";
            }
            String msg;
            Player player = null;
            if (sender instanceof ProxiedPlayer) {
                player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
                Rank rank = player.getRank();
                msg = ChatColor.WHITE + "[" + ChatColor.RED + "STAFF" + ChatColor.WHITE + "] " + rank.getNameWithBrackets()
                        + " " + ChatColor.GRAY + player.getName() + ": " + ChatColor.WHITE +
                        ChatColor.translateAlternateColorCodes('&', message);
            } else {
                msg = ChatColor.WHITE + "[" + ChatColor.RED + "STAFF" + ChatColor.WHITE + "] " + ChatColor.GRAY +
                        "Console: " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', message);
            }
            ChatUtil.staffChatMessage(msg);
            if (player != null) {
                ChatUtil.logMessage(player.getUniqueId(), "/sc " + player.getName() + " " + message);
            }
            return;
        }
        sender.sendMessage(new ComponentBuilder("/sc [Message]").color(ChatColor.RED).create());
    }
}