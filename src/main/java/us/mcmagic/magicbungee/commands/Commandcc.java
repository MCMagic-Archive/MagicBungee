package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.ChatUtil;

import java.util.List;

public class Commandcc extends MagicCommand {

    public Commandcc() {
        super(Rank.EARNINGMYEARS);
        tabCompletePlayers = true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        List<String> connectedChat = ChatUtil.getParkChatServers();
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent("Only players can use this!"));
            return;
        }
        if (args.length == 1) {
        }
        Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        if (connectedChat.contains(player.getServer().getInfo().getName())) {
            clearChat(true, player, connectedChat);
        } else {
            clearChat(false, player, connectedChat);
        }
    }

    private void clearChat(boolean connected, Player player, List<String> connectedChat) {
        TextComponent msg = new TextComponent();
        TextComponent msgname = new TextComponent();
        msg.setText("Chat has been cleared");
        msgname.setText("Chat has been cleared by " + player.getName());
        msg.setColor(ChatColor.DARK_AQUA);
        msgname.setColor(ChatColor.DARK_AQUA);
        TextComponent blank = new TextComponent();
        blank.setText(" ");
        if (connected) {
            for (Player tp : MagicBungee.getOnlinePlayers()) {
                if (tp == null) {
                    continue;
                }
                if (tp.getServer() == null) {
                    continue;
                }
                if (connectedChat.contains(tp.getServer().getInfo().getName())) {
                    if (tp.getRank().getRankId() < Rank.EARNINGMYEARS.getRankId()) {
                        for (int i = 0; i < 99; i++) {
                            tp.sendMessage(blank);
                        }
                        tp.sendMessage(blank);
                        tp.sendMessage(msg);
                    } else {
                        tp.sendMessage(blank);
                        tp.sendMessage(msgname);
                    }
                }
            }
        } else {
            for (Player tp : MagicBungee.getOnlinePlayers()) {
                if (tp == null) {
                    continue;
                }
                if (tp.getServer() == null) {
                    continue;
                }
                if (tp.getServer().getInfo().getName().equals(player.getServer().getInfo().getName())) {
                    if (tp.getRank().getRankId() < Rank.EARNINGMYEARS.getRankId()) {
                        for (int i = 0; i < 99; i++) {
                            tp.sendMessage(blank);
                        }
                        tp.sendMessage(blank);
                        tp.sendMessage(msg);
                    } else {
                        tp.sendMessage(blank);
                        tp.sendMessage(msgname);
                    }
                }
            }
        }
    }
}