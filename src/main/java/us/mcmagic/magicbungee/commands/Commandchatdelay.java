package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.listeners.PlayerChat;
import us.mcmagic.magicbungee.permissions.Rank;

public class Commandchatdelay extends MagicCommand {

    public Commandchatdelay() {
        super(Rank.CASTMEMBER);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            TextComponent msg = new TextComponent();
            msg.setText("The chat delay is currently " + (PlayerChat.getChatDelay() / 1000) + " seconds!");
            msg.addExtra(" To change this, provide a number! (/chatdelay [Time in seconds])");
            msg.setColor(ChatColor.GREEN);
            sender.sendMessage(msg);
            return;
        }
        String source = (sender instanceof ProxiedPlayer) ? sender.getName() : "Console";
        try {
            int time = Integer.parseInt(args[0]);
            PlayerChat.setChatDelay(time * 1000);
            MagicBungee.dashboard.changeChatDelay(time, source);
        } catch (NumberFormatException e) {
            TextComponent msg = new TextComponent();
            msg.setText("Please use a whole number :)");
            msg.setColor(ChatColor.RED);
            sender.sendMessage(msg);
        }
    }
}