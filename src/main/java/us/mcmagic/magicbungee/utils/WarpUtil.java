package us.mcmagic.magicbungee.utils;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.Player;

import java.io.*;
import java.util.UUID;

public class WarpUtil implements Listener {

    @EventHandler
    public void onPluginMessageReceive(PluginMessageEvent event) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            String subchannel = in.readUTF();
            if (subchannel.equals("UpdateWarps")) {
                event.setCancelled(true);
                String server = in.readUTF();
                for (String sname : ChatUtil.getParkChatServers()) {
                    ServerInfo s = MagicBungee.getProxyServer().getServerInfo(sname);
                    if (s.getName().toLowerCase().equals(server.toLowerCase())) {
                        continue;
                    }
                    updateWarps(s);
                }
                return;
            }
            if (subchannel.equals("MagicWarp")) {
                event.setCancelled(true);
                String uuid = in.readUTF();
                final String server = in.readUTF();
                final String warp = in.readUTF();
                final Player tp = MagicBungee.getPlayer(UUID.fromString(uuid));
                tp.setWarpOnJoin(warp);
                tp.setNeedsToWarp(true);
                MagicBungee.getProxyServer().getPlayer(tp.getUniqueId()).connect(MagicBungee.getProxyServer()
                        .getServerInfo(server));
            }
        } catch (Exception ignored) {
        }
    }

    public void updateWarps(ServerInfo server) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("UpdateWarps");
            server.sendData("BungeeCord", b.toByteArray());
        } catch (IOException ignored) {
        }
    }
}