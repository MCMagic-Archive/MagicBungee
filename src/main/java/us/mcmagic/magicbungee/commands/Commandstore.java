package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import us.mcmagic.magicbungee.handlers.MagicCommand;

/**
 * Created by Marc on 3/15/15
 */
public class Commandstore extends MagicCommand {
    public BaseComponent[] blank = new ComponentBuilder(" ").create();
    public BaseComponent[] msg = new ComponentBuilder("Click here to visit our Store").color(ChatColor.YELLOW).bold(true)
            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    new ComponentBuilder("Click to visit https://store.mcmagic.us").color(ChatColor.GREEN).create()))
            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://store.mcmagic.us")).create();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(blank);
        sender.sendMessage(msg);
        sender.sendMessage(blank);
    }
}