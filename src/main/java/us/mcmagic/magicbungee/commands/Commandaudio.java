package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.permissions.Rank;

/**
 * Created by Marc on 6/16/15
 */
public class Commandaudio extends MagicCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        boolean isStaff = !(sender instanceof ProxiedPlayer) || MagicBungee.getPlayer(((ProxiedPlayer) sender)
                .getUniqueId()).getRank().getRankId() >= Rank.CASTMEMBER.getRankId();
        if ((args.length == 1) && (args[0].equalsIgnoreCase("debug")) && isStaff) {
            sender.sendMessage(new ComponentBuilder("Key count: " +
                    MagicBungee.socketConnection.getSessionKeys().keySet().size()).create());
        } else if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            MagicBungee.socketConnection.generateAudioUrl(player);
        } else {
            TextComponent message = new TextComponent("Only players can use this command");
            message.setColor(ChatColor.RED);
            sender.sendMessage(message);
        }
    }
}