package us.mcmagic.magicbungee.utils;

import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.Player;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.UUID;

/**
 * Created by Marc on 3/21/15
 */
public class ResourceUtil implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            String subchannel = in.readUTF();
            if (subchannel.equals("RequestMagicPack")) {
                event.setCancelled(true);
                UUID sender = UUID.fromString(in.readUTF());
                Player player = MagicBungee.getPlayer(sender);
                if (player == null) {
                    MagicBungee.getProxyServer().getLogger().warning(sender + " was received in a PluginMessage, but can't be found!");
                    return;
                }
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                out.writeUTF("MagicPack");
                out.writeUTF(player.getUniqueId().toString());
                out.writeUTF(player.getCurrentPack());
                player.getServer().sendData("BungeeCord", b.toByteArray());
                return;
            }
            if (subchannel.equals("SetMagicPack")) {
                event.setCancelled(true);
                UUID sender = UUID.fromString(in.readUTF());
                String pack = in.readUTF();
                MagicBungee.getPlayer(sender).setCurrentPack(pack);
            }
        } catch (Exception ignored) {
        }
    }
}