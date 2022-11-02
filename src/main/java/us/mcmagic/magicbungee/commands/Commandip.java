package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;

/**
 * Created by Marc on 7/3/15
 */
public class Commandip extends MagicCommand {

    public Commandip() {
        super(Rank.CASTMEMBER);
        tabCompletePlayers = true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(new ComponentBuilder("/ip [Player]").color(ChatColor.RED).create());
            return;
        }
        Player tp = MagicBungee.getPlayer(args[0]);
        if (tp == null) {
            sender.sendMessage(new ComponentBuilder("That player wasn't found!").color(ChatColor.RED).create());
            return;
        }
        sender.sendMessage(new ComponentBuilder("IP of " + tp.getName() + " is " +
                tp.getAddress()).color(ChatColor.GREEN).create());
    }
}
