package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;

/**
 * Created by Marc on 4/17/16
 */
public class Commandmsgtoggle extends MagicCommand {

    public Commandmsgtoggle() {
        super(Rank.DEVELOPER);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        player.setRecieveMessages(!player.canRecieveMessages());
        if (player.canRecieveMessages()) {
            player.sendMessage(new ComponentBuilder("You have ").color(ChatColor.YELLOW).append("enabled ").color(ChatColor.GREEN)
                    .append("receiving private messages!").color(ChatColor.YELLOW).create());
        } else {
            player.sendMessage(new ComponentBuilder("You have ").color(ChatColor.YELLOW).append("disabled ").color(ChatColor.RED)
                    .append("receiving private messages!").color(ChatColor.YELLOW).create());
        }
    }
}