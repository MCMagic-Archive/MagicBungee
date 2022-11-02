package us.mcmagic.magicbungee.listeners;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.socket.packets.as.PacketServerSwitch;
import us.mcmagic.magicbungee.handlers.Player;

/**
 * Created by Marc on 1/16/15
 */
public class ServerSwitch implements Listener {

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer proxied = event.getPlayer();
        proxied.sendMessage(new TextComponent(" "));
        Player tp = MagicBungee.getPlayer(proxied.getUniqueId());
        if (tp == null) {
            return;
        }
        if (tp.needsToWarp()) {
            proxied.chat("/warp " + tp.getWarpOnJoin());
            tp.setNeedsToWarp(false);
            tp.setWarpOnJoin("");
        }
        PacketServerSwitch packet = new PacketServerSwitch(proxied.getServer().getInfo().getName());
        MagicBungee.socketConnection.sendMessage(proxied, packet.getJSON().toString());
        proxied.chat("/as loginsync");
    }
}
