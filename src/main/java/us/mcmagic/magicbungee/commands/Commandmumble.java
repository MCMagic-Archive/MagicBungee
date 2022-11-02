package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import us.mcmagic.magicbungee.handlers.MagicCommand;

import java.util.ArrayList;
import java.util.List;

public class Commandmumble extends MagicCommand {
    public static List<TextComponent> messages = new ArrayList<>();

    public Commandmumble() {
        TextComponent blank = new TextComponent();
        TextComponent msg = new TextComponent();
        blank.setText(" ");
        msg.setText("Click here to download Mumble");
        msg.setColor(ChatColor.YELLOW);
        msg.setBold(true);
        msg.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("Click to visit " +
                "https://mcmagic.us/mumble").color(ChatColor.GREEN).create()));
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://mcmagic.us/mumble"));
        messages.add(blank);
        messages.add(msg);
        messages.add(blank);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        for (TextComponent msg : messages) {
            sender.sendMessage(msg);
        }
    }
}