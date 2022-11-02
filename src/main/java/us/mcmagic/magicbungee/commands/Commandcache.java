package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.permissions.Rank;

/**
 * Created by Marc on 4/19/15
 */
public class Commandcache extends MagicCommand {

    public Commandcache() {
        super(Rank.CASTMEMBER);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(new ComponentBuilder("User Cache Size: ").color(ChatColor.GREEN)
                .append(MagicBungee.getInstance().getUserCache().size() + "").color(ChatColor.YELLOW).create());
    }
}
