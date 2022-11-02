package us.mcmagic.magicbungee.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.Ban;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.listeners.PlayerChat;
import us.mcmagic.magicbungee.permissions.Rank;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.sql.Date;
import java.util.UUID;

public class MagicBandUtil implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            String name = in.readUTF();
            if (name.equalsIgnoreCase("WDLProtect")) {
                Player player = MagicBungee.getPlayer(UUID.fromString(in.readUTF()));
                Ban ban = new Ban(player.getUniqueId(), player.getName(), false, System.currentTimeMillis() + 259200000,
                        "Attempting to use a World Downloader", "Dashboard");
                MagicBungee.dashboard.announceBan(ban);
                BanUtil.banPlayer(player.getUniqueId(), "Attempting to use a World Downloader", false,
                        new Date(System.currentTimeMillis() + 259200000), "Dashboard");
                player.kickPlayer(new ComponentBuilder("MCMagic does not authorize the use of World Downloader Mods!\n")
                        .color(ChatColor.RED).append("You have been temporarily banned for 3 Days.\n")
                        .color(ChatColor.YELLOW).append("If you believe this was a mistake, send an appeal at " +
                                "mcmagic.us/appeal.").color(ChatColor.YELLOW).create());
                event.setCancelled(true);
                return;
            }
            if (name.equals("AudioServer")) {
                event.setCancelled(true);
                String pname = in.readUTF();
                ProxiedPlayer player = MagicBungee.getProxyServer().getPlayer(pname);
                if (player != null) {
                    MagicBungee.socketConnection.sendMessage(player, in.readUTF());
                }
                return;
            }
            if (name.equals("ServerBroadcast")) {
                event.setCancelled(true);
                String username = in.readUTF();
                String message = in.readUTF();
                BaseComponent[] msg = TextComponent.fromLegacyText(ChatColor.WHITE + "[" + ChatColor.AQUA + "" +
                        "Information" + ChatColor.WHITE + "] " + ChatColor.GREEN +
                        ChatColor.translateAlternateColorCodes('&', message));
                BaseComponent[] staff = TextComponent.fromLegacyText(ChatColor.WHITE + "[" + ChatColor.AQUA + username +
                        ChatColor.WHITE + "] " + ChatColor.GREEN + ChatColor.translateAlternateColorCodes('&', message));
                for (ProxiedPlayer tp : MagicBungee.getProxyServer().getPlayers()) {
                    if (MagicBungee.getPlayer(tp.getUniqueId()).getRank().getRankId() >= Rank.CASTMEMBER.getRankId()) {
                        tp.sendMessage(staff);
                    } else {
                        tp.sendMessage(msg);
                    }
                }
                return;
            }
            if (name.equals("Uploaded")) {
                event.setCancelled(true);
                Player player = MagicBungee.getPlayer(UUID.fromString(in.readUTF()));
                if (player == null) {
                    return;
                }
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                out.writeUTF("Download");
                out.writeUTF(player.getUniqueId().toString());
                player.getServer().sendData("BungeeCord", b.toByteArray());
                return;
            }
            if (name.equals("ParkChatUnmute")) {
                event.setCancelled(true);
                if (PlayerChat.mutedServers.containsKey("ParkChat")) {
                    String msg = ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "MCMagic Chat" + ChatColor.WHITE + "] " +
                            ChatColor.YELLOW + "Chat has been unmuted";
                    String msgname = ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "MCMagic Chat" + ChatColor.WHITE + "] " +
                            ChatColor.YELLOW + "Chat has been unmuted by " + in.readUTF();
                    PlayerChat.mutedServers.remove("ParkChat");
                    for (Player tp : MagicBungee.getOnlinePlayers()) {
                        String server;
                        try {
                            server = tp.getServer().getInfo().getName();
                        } catch (Exception ignored) {
                            continue;
                        }
                        if (server != null) {
                            if (ChatUtil.isParkChat(tp.getServer().getInfo().getName())) {
                                if (tp.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                                    tp.sendMessage(msg);
                                } else {
                                    tp.sendMessage(msgname);
                                }
                            }
                        }
                    }
                }
                return;
            }
            if (name.equals("ParkChatMute")) {
                event.setCancelled(true);
                if (!PlayerChat.mutedServers.containsKey("ParkChat")) {
                    String msg = ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "MCMagic Chat" + ChatColor.WHITE + "] " +
                            ChatColor.YELLOW + "Chat has been muted";
                    String msgname = ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "MCMagic Chat" + ChatColor.WHITE + "] " +
                            ChatColor.YELLOW + "Chat has been muted by " + in.readUTF();
                    PlayerChat.mutedServers.put("ParkChat", true);
                    for (Player tp : MagicBungee.getOnlinePlayers()) {
                        String server;
                        try {
                            server = tp.getServer().getInfo().getName();
                        } catch (Exception ignored) {
                            continue;
                        }
                        if (server != null) {
                            if (ChatUtil.isParkChat(tp.getServer().getInfo().getName())) {
                                if (tp.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                                    tp.sendMessage(msg);
                                } else {
                                    tp.sendMessage(msgname);
                                }
                            }
                        }
                    }
                }
                return;
            }
            if (name.equals("UpdateHotelRooms")) {
                event.setCancelled(true);
                String server = in.readUTF();
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                out.writeUTF("UpdateHotelRooms");
                byte[] array = b.toByteArray();
                for (ServerInfo s : MagicBungee.getProxyServer().getServers().values()) {
                    if (!ChatUtil.isParkChat(s.getName()) || s.getName().equalsIgnoreCase(server)) {
                        continue;
                    }
                    if (s.getPlayers().size() == 0) {
                        continue;
                    }
                    s.sendData("BungeeCord", array);
                }
            }
        } catch (Exception ignored) {
        }
    }
}