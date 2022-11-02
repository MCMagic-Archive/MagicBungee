package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;

public class Commandoc extends MagicCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        TextComponent msg = new TextComponent();
        TextComponent blank = new TextComponent();
        msg.setText("Total Players Online: " + MagicBungee.getProxyServer().getPlayers().size());
        blank.setText(" ");
        msg.setColor(ChatColor.GREEN);
        sender.sendMessage(blank);
        sender.sendMessage(msg);
        sender.sendMessage(blank);
    }
}