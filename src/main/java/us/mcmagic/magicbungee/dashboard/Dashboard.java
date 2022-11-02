package us.mcmagic.magicbungee.dashboard;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.AddressBan;
import us.mcmagic.magicbungee.handlers.Ban;
import us.mcmagic.magicbungee.handlers.Mute;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.DateUtil;

/**
 * Created by Marc on 2/4/16
 */
public class Dashboard {

    public void announceBan(Ban ban) {
        sendMessage(new ComponentBuilder("[").color(ChatColor.WHITE).append("Dashboard").color(ChatColor.RED).append("] ")
                .color(ChatColor.WHITE).append(ban.getName() + " ").color(ChatColor.GREEN).append("was banned by ")
                .color(ChatColor.RED).append(ban.getSource() + " ").color(ChatColor.GREEN).append("Reason: ")
                .color(ChatColor.RED).append(ban.getReason() + " ").color(ChatColor.GREEN).append("Expires: ")
                .color(ChatColor.RED).append(ban.isPermanent() ? "Permanent" :
                        DateUtil.formatDateDiff(ban.getRelease())).create());
    }

    public void announceBan(AddressBan ban) {
        sendMessage(new ComponentBuilder("[").color(ChatColor.WHITE).append("Dashboard").color(ChatColor.RED).append("] ")
                .color(ChatColor.WHITE).append("IP " + ban.getAddress() + " ").color(ChatColor.GREEN)
                .append("was banned by ").color(ChatColor.RED).append(ban.getSource() + " ").color(ChatColor.GREEN)
                .append("Reason: ").color(ChatColor.RED).append(ban.getReason() + " ").color(ChatColor.GREEN).create());
    }

    public void announceUnban(String name, String source) {
        sendMessage(new ComponentBuilder("[").color(ChatColor.WHITE).append("Dashboard").color(ChatColor.RED).append("] ")
                .color(ChatColor.WHITE).append(name + " ").color(ChatColor.GREEN).append("has been unbanned by ")
                .color(ChatColor.RED).append(source + " ").create());
    }

    public void announceKick(String name, String reason, String source) {
        sendMessage(new ComponentBuilder("[").color(ChatColor.WHITE).append("Dashboard").color(ChatColor.RED).append("] ")
                .color(ChatColor.WHITE).append(name + " ").color(ChatColor.GREEN).append("was kicked by ")
                .color(ChatColor.RED).append(source + " ").color(ChatColor.GREEN).append("Reason: ")
                .color(ChatColor.RED).append(reason).color(ChatColor.GREEN).create());
    }

    public void announceMute(Mute mute) {
        sendMessage(new ComponentBuilder("[").color(ChatColor.WHITE).append("Dashboard").color(ChatColor.RED).append("] ")
                .color(ChatColor.WHITE).append(mute.getName() + " ").color(ChatColor.GREEN).append("was muted by ")
                .color(ChatColor.RED).append(mute.getSource() + " ").color(ChatColor.GREEN).append("Reason: ")
                .color(ChatColor.RED).append(mute.getReason() + " ").color(ChatColor.GREEN).append("Expires: ")
                .color(ChatColor.RED).append(DateUtil.formatDateDiff(mute.getRelease())).color(ChatColor.GREEN).create());
    }

    public void announceUnmute(Mute mute, String source) {
        sendMessage(new ComponentBuilder("[").color(ChatColor.WHITE).append("Dashboard").color(ChatColor.RED).append("] ")
                .color(ChatColor.WHITE).append(mute.getName() + " ").color(ChatColor.GREEN).append("has been unbanned by ")
                .color(ChatColor.RED).append(source + " ").create());
    }


    public void changeChatDelay(int time, String source) {
        sendMessage(new ComponentBuilder("[").color(ChatColor.WHITE).append("Dashboard").color(ChatColor.RED)
                .append("] ").color(ChatColor.WHITE).append("The chat delay was set to " + time + " seconds by " +
                        source).color(ChatColor.GREEN).create());
    }

    private void sendMessage(String s) {
        sendMessage(new ComponentBuilder(s).create());
    }

    private void sendMessage(BaseComponent[] msg) {
        for (Player player : MagicBungee.getOnlinePlayers()) {
            if (player.getRank().getRankId() >= Rank.EARNINGMYEARS.getRankId()) {
                try {
                    player.sendMessage(msg);
                } catch (Exception ignored) {
                }
            }
        }
    }
}