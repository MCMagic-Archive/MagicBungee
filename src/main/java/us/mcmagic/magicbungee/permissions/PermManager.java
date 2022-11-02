package us.mcmagic.magicbungee.permissions;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.Player;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.UUID;

public class PermManager implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            String name = in.readUTF();
            if (name.equals("RankChange")) {
                event.setCancelled(true);
                UUID uuid = UUID.fromString(in.readUTF());
                Rank rank = Rank.fromString(in.readUTF());
                Player tp = MagicBungee.getPlayer(uuid);
                if (tp == null) {
                    return;
                }
                final Rank previous = tp.getRank();
                tp.setRank(rank);
                tp.sendMessage(new ComponentBuilder("Your Rank has been changed from ").color(ChatColor.YELLOW)
                        .append(previous.getNameWithBrackets() + " ").append("to ").color(ChatColor.YELLOW)
                        .append(rank.getName()).create());
            }
        } catch (Exception ignored) {
        }
    }
}