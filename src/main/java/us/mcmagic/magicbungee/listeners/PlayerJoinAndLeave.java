package us.mcmagic.magicbungee.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.AddressBan;
import us.mcmagic.magicbungee.handlers.Ban;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.threads.StaffClock;
import us.mcmagic.magicbungee.utils.BanUtil;
import us.mcmagic.magicbungee.utils.ChatUtil;
import us.mcmagic.magicbungee.utils.DateUtil;
import us.mcmagic.magicbungee.utils.FriendUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Marc on 1/12/15
 */
public class PlayerJoinAndLeave implements Listener {
    public static boolean rebooting = false;

    @EventHandler
    public void onLogin(LoginEvent event) {
        PendingConnection connection = event.getConnection();
        if (!MagicBungee.canJoin) {
            event.setCancelled(true);
            event.setCancelReason(ChatColor.AQUA + "Players can not join right now. Try again in a few seconds!");
            return;
        }
        if (rebooting) {
            event.setCancelled(true);
            event.setCancelReason(ChatColor.AQUA + "We're restarting our servers right now! Check back in a few moments.");
            return;
        }
        MagicBungee.removePlayer(connection.getUniqueId());
        Player player = MagicBungee.createPlayer(connection.getUniqueId(), connection.getName(), connection.getAddress()
                .getAddress().toString().replaceAll("/", ""));
        if (MagicBungee.maintenance) {
            if (player.getRank().getRankId() < Rank.EARNINGMYEARS.getRankId()) {
                event.setCancelled(true);
                MagicBungee.removePlayer(connection.getUniqueId());
                event.setCancelReason(ChatColor.RED + "Sorry, we're in Maintenance Mode right now. Please check back later!");
            }
            return;
        }
        AddressBan addressBan = BanUtil.getAddressBan(player.getAddress());
        if (addressBan != null) {
            event.setCancelled(true);
            event.setCancelReason(ChatColor.RED + "Your IP Address (" + addressBan.getAddress() +
                    ") has been banned from this server!\n Appeal at " + ChatColor.AQUA + "https://mcmagic.us/appeal Reason: "
                    + addressBan.getReason());
            if (player != null) {
                MagicBungee.removePlayer(player.getUniqueId());
            }
            return;
        }
        String[] list = player.getAddress().split("\\.");
        String range = list[0] + "." + list[1] + "." + list[2] + ".*";
        AddressBan rangeBan = BanUtil.getAddressBan(range);
        if (rangeBan != null) {
            event.setCancelled(true);
            event.setCancelReason(ChatColor.RED + "Your IP Range (" + range + ") has been banned from this server!\nAppeal at "
                    + ChatColor.AQUA + "https://mcmagic.us/appeal Reason: " + rangeBan.getReason());
            if (player != null) {
                MagicBungee.removePlayer(player.getUniqueId());
            }
            return;
        }
        Ban ban = BanUtil.getBan(connection.getUniqueId(), connection.getName());
        if (ban != null) {
            event.setCancelled(true);
            if (ban.isPermanent()) {
                event.setCancelReason(ChatColor.RED + "You are banned from this server!\n Appeal at " +
                        ChatColor.AQUA + "https://mcmagic.us/appeal Reason: " + ban.getReason());
                if (player != null) {
                    MagicBungee.removePlayer(player.getUniqueId());
                }
            } else {
                if (ban.getRelease() <= System.currentTimeMillis()) {
                    BanUtil.unbanPlayer(connection.getUniqueId());
                    event.setCancelled(false);
                    return;
                }
                event.setCancelReason(ChatColor.RED + "You are temporarily banned from this server!\n Reason: "
                        + ban.getReason() + ChatColor.RED + " Release: " + ChatColor.AQUA + DateUtil.formatDateDiff(
                        ban.getRelease()));
                if (player != null) {
                    MagicBungee.removePlayer(player.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(final PostLoginEvent event) {
        MagicBungee.getProxyServer().getScheduler().schedule(MagicBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                UUID uuid = event.getPlayer().getUniqueId();
                try {
                    String name = event.getPlayer().getName();
                    String suuid = uuid.toString();
                    Player player = MagicBungee.getPlayer(uuid);
                    if (player == null) {
                        event.getPlayer().disconnect(new ComponentBuilder(
                                "We are currently experiencing some server-side issues. Please check back soon!")
                                .color(ChatColor.RED).create());
                        return;
                    }
                    if (player.getRank().getRankId() > Rank.EARNINGMYEARS.getRankId()) {
                        if (PlayerChat.mutedServers.containsKey("ParkChat")) {
                            player.sendMessage(new ComponentBuilder("\n\n\n\n\n      CHAT IS MUTED RIGHT NOW\n\n\n\n\n")
                                    .color(ChatColor.RED).bold(true).create());
                        }
                    }
                    //MagicBungee.audioServer.generateAudioUrl(MagicBungee.getProxyServer().getPlayer(player.getUniqueId()));
                    HashMap<UUID, String> cache = MagicBungee.getInstance().getUserCache();
                    if (cache.containsKey(uuid)) {
                        if (!cache.get(uuid).equals(name)) {
                            MagicBungee.getInstance().removeFromUserCache(uuid);
                            MagicBungee.getInstance().addToUserCache(uuid, name);
                        }
                    } else {
                        MagicBungee.getInstance().addToUserCache(uuid, name);
                    }
                    Rank rank = player.getRank();
                    if (player.getRank().getRankId() >= Rank.EARNINGMYEARS.getRankId()) {
                        BaseComponent[] smsg = new ComponentBuilder("[").color(ChatColor.WHITE).append("STAFF")
                                .color(ChatColor.RED).append("] [").color(ChatColor.WHITE).append(player.getRank()
                                        .getName()).color(rank.getTagColor()).append("] ").color(ChatColor.WHITE)
                                .append(player.getName() + " has clocked in.").color(ChatColor.YELLOW).create();
                        ChatUtil.staffChatMessage(smsg);
                        if (player.getRank().getRankId() >= Rank.EARNINGMYEARS.getRankId()) {
                            new StaffClock(player.getUniqueId(), "login", System.currentTimeMillis() / 1000).run();
                        }
                    }
                    HashMap<UUID, String> friends = FriendUtil.getFriendList(player.getUniqueId());
                    HashMap<UUID, String> requests = FriendUtil.getRequestList(player.getUniqueId());
                    player.setFriends(friends);
                    player.setRequests(requests);
                    HashMap<UUID, String> flist = player.getFriends();
                    if (!flist.isEmpty()) {
                        BaseComponent[] joinMessage = new ComponentBuilder(player.getName()).color(player.
                                getRank().getTagColor()).append(" has joined.").color(ChatColor.LIGHT_PURPLE)
                                .create();
                        if (player.getRank().getRankId() >= Rank.EARNINGMYEARS.getRankId()) {
                            for (Map.Entry<UUID, String> entry : flist.entrySet()) {
                                Player tp = MagicBungee.getPlayer(entry.getKey());
                                if (tp != null) {
                                    if (tp.getRank().getRankId() < Rank.EARNINGMYEARS.getRankId()) {
                                        tp.sendMessage(joinMessage);
                                    }
                                }
                            }
                        } else {
                            for (Map.Entry<UUID, String> entry : flist.entrySet()) {
                                Player tp = MagicBungee.getPlayer(entry.getKey());
                                if (tp != null) {
                                    tp.sendMessage(joinMessage);
                                }
                            }
                        }
                    }
                    HashMap<UUID, String> rlist = player.getRequests();
                    if (!rlist.isEmpty()) {
                        player.sendMessage(new ComponentBuilder("You have ").color(ChatColor.GREEN).append(rlist.size()
                                + "").color(ChatColor.YELLOW).bold(true).append(" Friend Requests!")
                                .color(ChatColor.GREEN).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder(ChatColor.GREEN + "Click here to list them!").create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend requests")).create());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (MagicBungee.getPlayer(uuid) == null) {
                        event.getPlayer().disconnect(new ComponentBuilder(
                                "We are currently experiencing some server-side issues. Please check back soon!")
                                .color(ChatColor.RED).create());
                    }
                }
            }
        }, 750, TimeUnit.MILLISECONDS);
    }

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        try {
            ProxiedPlayer proxied = event.getPlayer();
            MagicBungee.socketConnection.disconnect(proxied);
            Player player = MagicBungee.getPlayer(proxied.getUniqueId());
            logout(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout(Player player) {
        if (player == null) {
            return;
        }
        MagicBungee.partyUtil.logout(player);
        Rank rank = player.getRank();
        if (rank.getRankId() > Rank.CHARACTER.getRankId()) {
            BaseComponent[] smsg = new ComponentBuilder("[").color(ChatColor.WHITE).append("STAFF").color(ChatColor.RED)
                    .append("] [").color(ChatColor.WHITE).append(player.getRank().getName()).color(rank.getTagColor())
                    .append("] ").color(ChatColor.WHITE).append(player.getName() + " has clocked out.")
                    .color(ChatColor.YELLOW).create();
            ChatUtil.staffChatMessage(smsg);
        }
        HashMap<UUID, String> flist = player.getFriends();
        if (!flist.isEmpty()) {
            BaseComponent[] leaveMessage = new ComponentBuilder(player.getName()).color(player.getRank().getTagColor())
                    .append(" has left.").color(ChatColor.LIGHT_PURPLE).create();
            if (player.getRank().getRankId() >= Rank.EARNINGMYEARS.getRankId()) {
                for (Map.Entry<UUID, String> entry : flist.entrySet()) {
                    Player tp = MagicBungee.getPlayer(entry.getKey());
                    if (tp != null) {
                        if (tp.getRank().getRankId() < Rank.EARNINGMYEARS.getRankId()) {
                            tp.sendMessage(leaveMessage);
                        }
                    }
                }
            } else {
                for (Map.Entry<UUID, String> entry : flist.entrySet()) {
                    Player tp = MagicBungee.getPlayer(entry.getKey());
                    if (tp != null) {
                        tp.sendMessage(leaveMessage);
                    }
                }
            }
        }
        MagicBungee.playerLogout(player);
    }
}