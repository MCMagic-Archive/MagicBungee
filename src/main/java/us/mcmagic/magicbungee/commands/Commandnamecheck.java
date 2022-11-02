package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.NameUtil;

import java.util.List;

@SuppressWarnings("deprecation")
public class Commandnamecheck extends MagicCommand {

    public Commandnamecheck() {
        super(Rank.CASTMEMBER);
        tabCompletePlayers = true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(new ComponentBuilder("/namecheck [username]").color(ChatColor.RED).create());
            return;
        }
        List<String> names = NameUtil.getNames(args[0]);
        if (names.size() < 2 || names.isEmpty()) {
            sender.sendMessage(new ComponentBuilder("That user could not be found!").color(ChatColor.RED).create());
            return;
        }
        sender.sendMessage(new ComponentBuilder("Previous usernames of [" + args[0] + "] - (" + names.get(0) + ") " +
                "are").color(ChatColor.GREEN).create());
        for (int i = 1; i < names.size(); i++) {
            sender.sendMessage(new ComponentBuilder("- ").color(ChatColor.AQUA).append(names.get(i))
                    .color(ChatColor.GREEN).create());
        }
    }
}
