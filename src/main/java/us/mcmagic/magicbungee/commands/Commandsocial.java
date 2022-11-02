package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import us.mcmagic.magicbungee.handlers.MagicCommand;

/**
 * Created by Marc on 1/26/15
 */
public class Commandsocial extends MagicCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(new ComponentBuilder("MCMagic Social Links:").color(ChatColor.YELLOW).bold(true).create());
        sender.sendMessage(new ComponentBuilder("Website: ").color(ChatColor.GREEN).append("https://mcmagic.us")
                .color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://mcmagic.us")).create());
        sender.sendMessage(new ComponentBuilder("Twitter: ").color(ChatColor.GREEN).append("https://twitter.com/MCMagicParks")
                .color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://twitter.com/MCMagicParks"))
                .create());
        sender.sendMessage(new ComponentBuilder("Beam: ").color(ChatColor.GREEN).append("https://beam.pro/MCMagicParks")
                .color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://beam.pro/MCMagicParks"))
                .create());
        sender.sendMessage(new ComponentBuilder("YouTube: ").color(ChatColor.GREEN).append("https://youtube.com/MCMagicParks")
                .color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://youtube.com/MCMagicParks"))
                .create());
        sender.sendMessage(new ComponentBuilder("Mumble: ").color(ChatColor.GREEN).append("Click here for more info")
                .color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://mcmagic.us/mumble"))
                .create());
        sender.sendMessage(new ComponentBuilder("Instagram: ").color(ChatColor.GREEN).append("https://instagram.com/MCMagicParks")
                .color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://instagram.com/MCMagicParks"))
                .create());
        sender.sendMessage(new ComponentBuilder("Facebook: ").color(ChatColor.GREEN).append("https://facebook.com/MCMagicParks")
                .color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://facebook.com/MCMagicParks"))
                .create());
    }
}