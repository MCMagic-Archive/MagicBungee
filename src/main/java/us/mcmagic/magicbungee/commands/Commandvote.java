package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.permissions.Rank;

import java.util.Map;

/**
 * Created by Marc on 6/21/15
 */
public class Commandvote extends MagicCommand {

    public Commandvote() {
        super(Rank.DEVELOPER);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(new ComponentBuilder("Vote for our server and receive ").color(ChatColor.GREEN)
                .append("Tokens!").color(ChatColor.GOLD).bold(true).create());
        for (Map.Entry<String, String> entry : MagicBungee.statUtil.servers.entrySet()) {
            sender.sendMessage(new ComponentBuilder("- ").color(ChatColor.AQUA).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("Click to vote on this site!").event(new ClickEvent(ClickEvent.Action.OPEN_URL,
                            entry.getValue())).color(ChatColor.GREEN).create())).append(entry.getKey())
                    .color(ChatColor.GREEN).create());
        }
    }
}