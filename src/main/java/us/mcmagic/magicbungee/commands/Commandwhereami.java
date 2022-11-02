package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;

public class Commandwhereami extends MagicCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
            Server server = player.getServer();
            player.sendMessage(ChatColor.BLUE + "You are on the server "
                    + ChatColor.GOLD + server.getInfo().getName());
        } else {
            sender.sendMessage(new ComponentBuilder("Only Proxied Players can use this command!").color(ChatColor.RED).create());
        }
    }
}