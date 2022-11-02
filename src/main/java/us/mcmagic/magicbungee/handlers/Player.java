package us.mcmagic.magicbungee.handlers;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.permissions.Rank;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Marc on 1/10/15
 */
public class Player {
    private UUID uuid;
    private String username;
    private Rank rank;
    private long loginTime;
    private boolean friendToggled;
    private HashMap<UUID, String> friends = new HashMap<>();
    private HashMap<UUID, String> requests = new HashMap<>();
    private Mute mute;
    private String address;
    private String currentPack = "none";
    private long afkTime;
    private UUID afkCaptcha;
    private String warpOnJoin = "";
    private boolean needsToWarp = false;
    private boolean kicking = false;
    private boolean mentions = true;
    private boolean recieveMessages = true;

    public Player(UUID uuid, String username, Rank rank, boolean friendToggled,
                  Mute mute, String address, boolean mentions) {
        this.uuid = uuid;
        this.username = username;
        this.rank = rank;
        this.friendToggled = friendToggled;
        loginTime = System.currentTimeMillis();
        this.mute = mute;
        this.address = address;
        afkTime = System.currentTimeMillis();
        afkCaptcha = uuid;
        this.mentions = mentions;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return username;
    }

    public String getCurrentPack() {
        return currentPack;
    }

    public void setCurrentPack(String pack) {
        this.currentPack = pack;
    }

    public void sendMessage(String message) {
        ProxiedPlayer p = MagicBungee.getProxyServer().getPlayer(uuid);
        if (p != null) {
            p.sendMessage(TextComponent.fromLegacyText(message));
        }
    }


    public void sendMessage(TextComponent message) {
        ProxiedPlayer p = MagicBungee.getProxyServer().getPlayer(uuid);
        if (p != null) {
            p.sendMessage(message);
        }
    }

    public void sendMessage(BaseComponent[] components) {
        ProxiedPlayer p = MagicBungee.getProxyServer().getPlayer(uuid);
        if (p != null) {
            p.sendMessage(components);
        }
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public void kickPlayer(String reason) {
        if (kicking) {
            return;
        }
        kicking = true;
        BaseComponent[] r = new ComponentBuilder("You have been disconnected for: ").color(ChatColor.RED)
                .append(reason).color(ChatColor.AQUA).create();
        MagicBungee.getProxyServer().getPlayer(uuid).disconnect(r);
        MagicBungee.getOnlinePlayers().remove(this);
    }

    public void kickPlayer(TextComponent reason) {
        if (kicking) {
            return;
        }
        kicking = true;
        MagicBungee.getProxyServer().getPlayer(uuid).disconnect(reason);
        MagicBungee.getOnlinePlayers().remove(this);
    }

    public void kickPlayer(BaseComponent[] reason) {
        if (kicking) {
            return;
        }
        kicking = true;
        MagicBungee.getProxyServer().getPlayer(uuid).disconnect(reason);
        MagicBungee.getOnlinePlayers().remove(this);
    }

    public boolean isKicking() {
        return kicking;
    }

    public HashMap<UUID, String> getFriends() {
        return friends;
    }

    public boolean hasFriendToggledOff() {
        return friendToggled;
    }

    public void setHasFriendToggled(boolean bool) {
        friendToggled = bool;
    }

    public HashMap<UUID, String> getRequests() {
        return requests;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public Server getServer() {
        ProxiedPlayer p = MagicBungee.getProxyServer().getPlayer(uuid);
        if (p != null) {
            return p.getServer();
        } else {
            return null;
        }
    }

    public String getAddress() {
        return address;
    }

    public Mute getMute() {
        return mute;
    }

    public long getAfkTime() {
        return afkTime;
    }

    public void afkAction() {
        afkTime = System.currentTimeMillis();
    }

    public void captchaAccept() {
        afkCaptcha = uuid;
    }

    public UUID createAfkCaptcha() {
        afkCaptcha = UUID.randomUUID();
        return afkCaptcha;
    }

    public UUID getCaptcha() {
        return afkCaptcha;
    }

    public boolean captchaMatches(String msg) {
        if (afkCaptcha == uuid) {
            return false;
        }
        try {
            return afkCaptcha.toString().equals(msg.split(" ")[1]);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasCaptcha() {
        return afkCaptcha != uuid;
    }

    public boolean needsToWarp() {
        return needsToWarp;
    }

    public String getWarpOnJoin() {
        return warpOnJoin;
    }

    public void setWarpOnJoin(String warpOnJoin) {
        this.warpOnJoin = warpOnJoin;
    }

    public void setNeedsToWarp(boolean needsToWarp) {
        this.needsToWarp = needsToWarp;
    }

    public String getServerName() {
        try {
            ServerInfo info = getServer().getInfo();
            return info.getName();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public void setFriends(HashMap<UUID, String> friends) {
        this.friends = friends;
    }

    public void setRequests(HashMap<UUID, String> requests) {
        this.requests = requests;
    }

    public boolean hasMentions() {
        return mentions;
    }

    public void setMentions(boolean mentions) {
        this.mentions = mentions;
    }

    public boolean canRecieveMessages() {
        return recieveMessages;
    }

    public void setRecieveMessages(boolean recieveMessages) {
        this.recieveMessages = recieveMessages;
    }
}