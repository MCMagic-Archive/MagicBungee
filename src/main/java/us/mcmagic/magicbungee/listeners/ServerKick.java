package us.mcmagic.magicbungee.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.Player;

/**
 * Created by Marc on 1/16/15
 */
public class ServerKick implements Listener {

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        Player player = MagicBungee.getPlayer(event.getPlayer().getUniqueId());
        ServerInfo server = event.getKickedFrom();
        String name = server.getName();
        if (event.getState().equals(ServerKickEvent.State.CONNECTED)) {
            event.setCancelled(true);
            player.sendMessage(new ComponentBuilder("The server you were previously on (").color(ChatColor.RED)
                    .append(server.getName()).color(ChatColor.AQUA).append(") has disconnected you with the reason (\"")
                    .color(ChatColor.RED).append(event.getKickReasonComponent()[0].toPlainText()).color(ChatColor.AQUA)
                    .append("\")").color(ChatColor.RED).create());
            if (name.startsWith("s") || name.startsWith("Show") || name.equals("Parkour") || name.equals("TTC")) {
                event.setCancelServer(MagicBungee.getProxyServer().getServerInfo("Arcade"));
            } else {
                event.setCancelServer(MagicBungee.getProxyServer().getServerInfo("TTC"));
            }
        }
    }
}
