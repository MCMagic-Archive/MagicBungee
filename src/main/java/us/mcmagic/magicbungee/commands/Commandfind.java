package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.permissions.Rank;

/**
 * Created by Marc on 7/2/15
 */
public class Commandfind extends MagicCommand {

    public Commandfind() {
        super(Rank.CASTMEMBER);
        tabCompletePlayers = true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(new ComponentBuilder("/find [Player]").color(ChatColor.RED).create());
            return;
        }
        ProxiedPlayer tp = MagicBungee.getProxyServer().getPlayer(args[0]);
        if (tp == null) {
            sender.sendMessage(new ComponentBuilder(args[0] + " is not online!").color(ChatColor.RED).create());
            return;
        }
        sender.sendMessage(new ComponentBuilder(tp.getName() + " is on the server ").color(ChatColor.BLUE)
                .append(tp.getServer().getInfo().getName()).color(ChatColor.GOLD).create());
    }
}
