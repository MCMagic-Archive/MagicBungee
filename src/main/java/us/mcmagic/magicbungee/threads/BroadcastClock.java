package us.mcmagic.magicbungee.threads;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import us.mcmagic.magicbungee.MagicBungee;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BroadcastClock {
    private static int number;
    private static List<BaseComponent[]> messages = new ArrayList<>();

    public static void initialize() {
        String prefix = "✴";
        ChatColor prefixColor = ChatColor.BLUE;
        String tweet = "https://twitter.com/intent/tweet?via=MCMagicParks&text=I%27m%20celebrating%205%20years%20of%20magic%20on%20MCMagic%21&hashtags=LiveYourDreams";
        messages.add(new ComponentBuilder("[").color(ChatColor.WHITE).append(prefix).color(prefixColor).append("] ")
                .color(ChatColor.WHITE).append("Now celebrating ").color(ChatColor.GREEN)
                .append("5 years ").color(ChatColor.YELLOW).bold(true).append("of ", ComponentBuilder.FormatRetention.NONE)
                .color(ChatColor.GREEN).append("M").color(ChatColor.RED).append("a").color(ChatColor.GOLD).append("g")
                .color(ChatColor.YELLOW).append("i").color(ChatColor.GREEN).append("c! ").color(ChatColor.BLUE)
                .append("#LiveYourDreams").color(ChatColor.GREEN).underlined(true)
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, tweet)).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Celebrate our anniversary on Twitter! #LiveYourDreams")
                                .color(ChatColor.GREEN).create())).create());
        messages.add(new ComponentBuilder("[").color(ChatColor.WHITE).append(prefix).color(prefixColor).append("] ")
                .color(ChatColor.WHITE).append("Welcome to the most ").color(ChatColor.AQUA).append("M").color(ChatColor.RED)
                .append("a").color(ChatColor.GOLD).append("g").color(ChatColor.YELLOW).append("i").color(ChatColor.GREEN)
                .append("c").color(ChatColor.BLUE).append("a").color(ChatColor.LIGHT_PURPLE).append("l")
                .color(ChatColor.DARK_PURPLE).append(" place in Minecraft!").color(ChatColor.AQUA).create());
        messages.add(new ComponentBuilder("[").color(ChatColor.WHITE).append(prefix).color(prefixColor).append("] ")
                .color(ChatColor.WHITE).append("Try out our new ").color(ChatColor.GREEN).append("Audio Server! ")
                .color(ChatColor.BLUE).append("Just type ").color(ChatColor.GREEN).append("/audio ").color(ChatColor.BLUE)
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to run this command!")
                        .color(ChatColor.GREEN).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                        "/audio")).append("to connect!").color(ChatColor.GREEN).create());
        messages.add(new ComponentBuilder("[").color(ChatColor.WHITE).append(prefix).color(prefixColor).append("] ")
                .color(ChatColor.WHITE).append("We are honored to be hosted by ").color(ChatColor.AQUA).append("MCProHosting!")
                .color(ChatColor.GREEN).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click to visit MCProHosting!").color(ChatColor.GREEN).create()))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://mcprohosting.com/?promo=mcmagic")).create());
        messages.add(new ComponentBuilder("[").color(ChatColor.WHITE).append(prefix).color(prefixColor).append("] ")
                .color(ChatColor.WHITE).append("Play some Games on our ").color(ChatColor.GREEN).append("Arcade ")
                .color(ChatColor.BLUE).append("at ").color(ChatColor.GREEN).append("/join Arcade").color(ChatColor.AQUA)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join Arcade"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join the Arcade")
                        .color(ChatColor.AQUA).create())).create());
        messages.add(new ComponentBuilder("[").color(ChatColor.WHITE).append(prefix).color(prefixColor).append("] ")
                .color(ChatColor.WHITE).append("Make some friends using our ").color(ChatColor.GREEN).append("Friend System! ")
                .color(ChatColor.YELLOW).append("Type ").color(ChatColor.GREEN).append("/friend ").event(
                        new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click to run this command!").color(ChatColor.GREEN).create()))
                .color(ChatColor.AQUA).append("to get started!", ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                .create());
        messages.add(new ComponentBuilder("[").color(ChatColor.WHITE).append(prefix).color(prefixColor).append("] ")
                .color(ChatColor.WHITE).append("Visit our store to become a DVC Member or Shareholder! ").color(ChatColor.GREEN)
                .append("https://store.mcmagic.us").color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        "https://store.mcmagic.us")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click to visit our Store").color(ChatColor.GREEN).create())).create());
        messages.add(new ComponentBuilder("[").color(ChatColor.WHITE).append(prefix).color(prefixColor).append("] ")
                .color(ChatColor.WHITE).append("Type ").color(ChatColor.GREEN).append("/social ").color(ChatColor.AQUA)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/social")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click to run this command!").color(ChatColor.GREEN).create()))
                .append("for all of our social media links!", ComponentBuilder.FormatRetention.NONE)
                .color(ChatColor.GREEN).create());
        messages.add(new ComponentBuilder("[").color(ChatColor.WHITE).append(prefix).color(prefixColor).append("] ")
                .color(ChatColor.WHITE).append("Please keep your language ").color(ChatColor.GREEN)
                .append("safe for little ears.").color(ChatColor.AQUA).create());
        messages.add(new ComponentBuilder("[").color(ChatColor.WHITE).append(prefix).color(prefixColor).append("] ")
                .color(ChatColor.WHITE).append("Experience a bug? Report it to us by typing ").color(ChatColor.GREEN)
                .append("/bug!").color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click to run /bug!").color(ChatColor.YELLOW).create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bug")).create());
        start();
    }

    private static void start() {
        MagicBungee.getProxyServer().getScheduler().schedule(MagicBungee.getInstance(), new Runnable() {
            public void run() {
                if (number == messages.size()) {
                    number = 0;
                }
                MagicBungee.getProxyServer().broadcast(messages.get(number));
                number++;
            }
        }, 20, 300, TimeUnit.SECONDS).getId();
    }
}