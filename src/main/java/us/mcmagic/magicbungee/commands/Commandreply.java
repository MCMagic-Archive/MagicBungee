package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Mute;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.listeners.PlayerChat;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.socket.packets.dashboard.PacketChatMessage;
import us.mcmagic.magicbungee.utils.ChatUtil;
import us.mcmagic.magicbungee.utils.DateUtil;
import us.mcmagic.magicbungee.utils.MuteUtil;

import java.util.Collections;
import java.util.Date;

@SuppressWarnings("deprecation")
public class Commandreply extends MagicCommand {

    public Commandreply() {
        aliases = Collections.singletonList("r");
        tabCompletePlayers = true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return;
        }
        Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        Mute mute = player.getMute();
        if (mute == null) {
            player.sendMessage(new ComponentBuilder("Please try chatting again in a moment. (Error Code 109)")
                    .color(ChatColor.RED).create());
            return;
        }
        if (mute.isMuted()) {
            long releaseTime = mute.getRelease();
            Date currentTime = new Date();
            if (currentTime.getTime() > releaseTime) {
                MuteUtil.unmutePlayer(player.getUniqueId());
                player.getMute().setMuted(false);
            } else {
                String msg = ChatColor.RED + "You are silenced! You will be unsilenced in " +
                        DateUtil.formatDateDiff(mute.getRelease()) + ".";
                if (!mute.getReason().equals("")) {
                    msg += " Reason: " + player.getMute().getReason();
                }
                player.sendMessage(msg);
                return;
            }
        }
        if (!ChatUtil.msgReply.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have anyone to respond to!");
            return;
        }
        Player tp = MagicBungee.getPlayer(ChatUtil.msgReply.get(player.getUniqueId()));
        if (tp == null) {
            return;
        }
        if (!MagicBungee.getOnlinePlayers().contains(tp)) {
            player.sendMessage(ChatColor.RED + "You don't have anyone to respond to!");
            ChatUtil.msgReply.remove(player.getUniqueId());
            return;
        }
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/reply [Message]");
            return;
        }
        if (!tp.canRecieveMessages() && player.getRank().getRankId() < Rank.EARNINGMYEARS.getRankId()) {
            player.sendMessage(new ComponentBuilder("This person has messages disabled!").color(ChatColor.RED).create());
            return;
        }
        String msg = "";
        for (String arg : args) {
            msg += arg + " ";
        }
        msg = player.getRank().getRankId() < Rank.EARNINGMYEARS.getRankId() ? PlayerChat.removeCaps(player, msg) : msg;
        if (player.getRank().getRankId() < Rank.EARNINGMYEARS.getRankId()) {
            if (PlayerChat.containsSwear(player, msg) || PlayerChat.isAdvert(player, msg)
                    || PlayerChat.spamCheck(player, msg) || PlayerChat.containsUnicode(player, msg)) {
                return;
            }
            String mm = msg.toLowerCase().replace(".", "").replace("-", "").replace(",", "")
                    .replace("/", "").replace("_", "").replace(" ", "");
            if (mm.contains("skype")) {
                player.sendMessage(new ComponentBuilder("Please do not ask for Skype information!")
                        .color(ChatColor.RED).bold(true).create());
                return;
            }
        }
        tp.sendMessage(player.getRank().getNameWithBrackets() + " " + ChatColor.GRAY + player.getName() + ChatColor.AQUA
                + " -> " + ChatColor.LIGHT_PURPLE + "you: " + ChatColor.WHITE + msg);
        if (tp.hasMentions()) {
            ChatUtil.mentionSound(tp);
        }
        player.sendMessage(ChatColor.LIGHT_PURPLE + "you" + ChatColor.AQUA + " -> " + tp.getRank().getNameWithBrackets()
                + " " + ChatColor.GRAY + tp.getName() + ": " + ChatColor.WHITE + msg);
        if (ChatUtil.msgReply.containsKey(player.getUniqueId())) {
            ChatUtil.msgReply.remove(player.getUniqueId());
        }
        if (ChatUtil.msgReply.containsKey(tp.getUniqueId())) {
            ChatUtil.msgReply.remove(tp.getUniqueId());
        }
        ChatUtil.msgReply.put(player.getUniqueId(), tp.getUniqueId());
        ChatUtil.msgReply.put(tp.getUniqueId(), player.getUniqueId());
        ChatUtil.socialSpyMessage(player, tp, msg, "r");
        PacketChatMessage packet = new PacketChatMessage(player.getUniqueId(), player.getName(),
                "/r " + tp.getName() + " " + msg, ChatUtil.isParkChat(player.getServerName()),
                player.getServerName(), true);
        MagicBungee.socketConnection.sendMessage(packet);
        ChatUtil.logMessage(player.getUniqueId(), "/r " + tp.getName() + " " + msg);
    }
}