package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import us.mcmagic.magicbungee.handlers.MagicCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Marc on 7/4/15
 */
public class Commandhelp extends MagicCommand {
    private List<BaseComponent[]> list = new ArrayList<>();

    public Commandhelp() {
        list.add(new ComponentBuilder("MCMagic Help Menu:").color(ChatColor.GREEN).create());
        list.add(new ComponentBuilder("- ").color(ChatColor.GREEN).append("/join ").color(ChatColor.AQUA)
                .append("- Join another Park or Server").color(ChatColor.YELLOW).create());
        list.add(new ComponentBuilder("- ").color(ChatColor.GREEN).append("/msg [Player] [Message] ")
                .color(ChatColor.AQUA).append("- Message another Player").color(ChatColor.YELLOW).create());
        list.add(new ComponentBuilder("- ").color(ChatColor.GREEN).append("/reply [Message]").color(ChatColor.AQUA)
                .append("- Reply to a message").color(ChatColor.YELLOW).create());
        list.add(new ComponentBuilder("- ").color(ChatColor.GREEN).append("/friend ").color(ChatColor.AQUA)
                .append("- Add some Friends!").color(ChatColor.YELLOW).create());
        list.add(new ComponentBuilder("- ").color(ChatColor.GREEN).append("/party ").color(ChatColor.AQUA)
                .append("- Create a Party!").color(ChatColor.YELLOW).create());
        list.add(new ComponentBuilder("- ").color(ChatColor.GREEN).append("/warp [Warp Name] ").color(ChatColor.AQUA)
                .append("- Warp to a location").color(ChatColor.YELLOW).create());
        list.add(new ComponentBuilder("- ").color(ChatColor.GREEN).append("/rp ").color(ChatColor.AQUA)
                .append("- Download our Resource Pack").color(ChatColor.YELLOW).create());
        list.add(new ComponentBuilder("- ").color(ChatColor.GREEN).append("/social ").color(ChatColor.AQUA)
                .append("- View all of our Social Links").color(ChatColor.YELLOW).create());
        list.add(new ComponentBuilder("Still have a question? Ask a ").color(ChatColor.YELLOW).append("Cast Member")
                .color(ChatColor.GREEN).append(" on the server!", ComponentBuilder.FormatRetention.NONE)
                .color(ChatColor.YELLOW).create());
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        for (BaseComponent[] msg : list) {
            sender.sendMessage(msg);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return Arrays.asList("");
    }
}