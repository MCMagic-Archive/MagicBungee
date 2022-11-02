package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.utils.FriendUtil;

import java.util.Collections;

/**
 * Created by Marc on 7/5/16
 */
public class Commandfriend extends MagicCommand {

    public Commandfriend() {
        aliases = Collections.singletonList("f");
        tabCompletePlayers = true;
    }

    @Override
    public void execute(CommandSender sender, String label, final String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new TextComponent(ChatColor.RED
                    + "Only players can use this command!"));
            return;
        }
        final Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        switch (args.length) {
            case 1:
                switch (args[0].toLowerCase()) {
                    case "help":
                        FriendUtil.helpMenu(player);
                        return;
                    case "list":
                        FriendUtil.listFriends(player, 1);
                        return;
                    case "toggle":
                        MagicBungee.getProxyServer().getScheduler().runAsync(MagicBungee.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                player.setHasFriendToggled(!player.hasFriendToggledOff());
                                if (player.hasFriendToggledOff()) {
                                    player.sendMessage(new ComponentBuilder("Friend Requests have been toggled ").color(ChatColor.YELLOW)
                                            .append("OFF").color(ChatColor.RED).create());
                                } else {
                                    player.sendMessage(new ComponentBuilder("Friend Requests have been toggled ").color(ChatColor.YELLOW)
                                            .append("ON").color(ChatColor.GREEN).create());
                                }
                                FriendUtil.toggleRequests(player);
                            }
                        });
                        return;
                    case "requests":
                        FriendUtil.listRequests(player);
                        return;
                }
                return;
            case 2:
                switch (args[0].toLowerCase()) {
                    case "list":
                        if (!isInt(args[1])) {
                            FriendUtil.listFriends(player, 1);
                            return;
                        }
                        FriendUtil.listFriends(player, Integer.parseInt(args[1]));
                        return;
                    case "tp":
                        String user = args[1];
                        Player tp = MagicBungee.getPlayer(user);
                        if (tp == null) {
                            player.sendMessage(new ComponentBuilder("Player not found!").color(ChatColor.RED).create());
                            return;
                        }
                        if (!player.getFriends().containsKey(tp.getUniqueId())) {
                            player.sendMessage(new ComponentBuilder(tp.getName()).color(ChatColor.GREEN)
                                    .append(" is not on your Friend List!").color(ChatColor.RED).create());
                            return;
                        }
                        FriendUtil.teleportPlayer(player, tp);
                        return;
                    case "add":
                        FriendUtil.addFriend(player, args[1]);
                        return;
                    case "remove":
                        FriendUtil.removeFriend(player, args[1]);
                        return;
                    case "accept":
                        FriendUtil.acceptFriend(player, args[1]);
                        return;
                    case "deny":
                        FriendUtil.denyFriend(player, args[1]);
                        return;
                }
        }
        FriendUtil.helpMenu(player);
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}