package us.mcmagic.magicbungee.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FriendUtil {

    public static void teleportPlayer(Player player, Player target) {
        if (target == null) {
            return;
        }
        if (player.getServerName().equals(target.getServerName())) {
            player.sendMessage(new ComponentBuilder("You're already on the same server as ").color(ChatColor.RED)
                    .append(target.getName() + "!").color(ChatColor.AQUA).create());
            return;
        }
        try {
            MagicBungee.getProxyServer().getPlayer(player.getUniqueId()).connect(target.getServer().getInfo());
            player.sendMessage(new ComponentBuilder("You connected to the server ").color(ChatColor.BLUE)
                    .append(target.getName() + " ").color(ChatColor.GREEN).append("is on! (" +
                            target.getServerName() + ")").color(ChatColor.BLUE).create());
        } catch (Exception ignored) {
        }
    }

    public static void listFriends(final Player player, int page) {
        HashMap<UUID, String> friends = player.getFriends();
        if (friends.isEmpty()) {
            player.sendMessage(" ");
            player.sendMessage(ChatColor.RED + "Type /friend add [Player] to add someone");
            player.sendMessage(" ");
            return;
        }
        int listSize = friends.size();
        int maxPage = (int) Math.ceil((double) friends.size() / 8);
        if (page > maxPage) {
            page = maxPage;
        }
        int startAmount = 8 * (page - 1);
        int endAmount;
        if (maxPage > 1) {
            if (page < maxPage) {
                endAmount = (8 * page);
            } else {
                endAmount = listSize;
            }
        } else {
            endAmount = listSize;
        }
        List<String> currentFriends = new ArrayList<>();
        for (Map.Entry<UUID, String> entry : friends.entrySet()) {
            currentFriends.add(entry.getValue());
        }
        Collections.sort(currentFriends);
        List<String> fsOnPage = new ArrayList<>();
        for (String s : currentFriends.subList(startAmount, endAmount)) {
            fsOnPage.add(s);
        }
        List<BaseComponent[]> list = new ArrayList<>();
        for (String s : fsOnPage) {
            if (MagicBungee.getPlayer(s) != null) {
                list.add(new ComponentBuilder("- ").color(ChatColor.AQUA).append(s).color(ChatColor.GREEN)
                        .event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("Click to join this player's server!")
                                .color(ChatColor.GREEN).create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/friend tp " + s)).create());
            } else {
                list.add(new ComponentBuilder("- ").color(ChatColor.AQUA).append(s).color(ChatColor.RED)
                        .event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("This player is offline!")
                                .color(ChatColor.RED).create())).create());
            }
        }
        player.sendMessage(new ComponentBuilder("Friend List ").color(ChatColor.YELLOW).append("[Page " + page + " of "
                + maxPage + "]").color(ChatColor.GREEN).create());
        for (BaseComponent[] s : list) {
            player.sendMessage(s);
        }
        if (list.size() > 8) {
            player.sendMessage(new ComponentBuilder("Scroll up for the full list!").color(ChatColor.GREEN).create());
        }
        player.sendMessage(" ");
    }

    public static void toggleRequests(Player player) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET toggled=? WHERE uuid=?");
            sql.setInt(1, player.hasFriendToggledOff() ? 1 : 0);
            sql.setString(2, player.getUniqueId().toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void listRequests(final Player player) {
        HashMap<UUID, String> requests = player.getRequests();
        if (requests.isEmpty()) {
            player.sendMessage(" ");
            player.sendMessage(ChatColor.RED + "You currently have no Friend Requests!");
            player.sendMessage(" ");
            return;
        }
        player.sendMessage(ChatColor.GREEN + "Request List:");
        for (Map.Entry<UUID, String> entry : requests.entrySet()) {
            player.sendMessage(new ComponentBuilder("- ").color(ChatColor.AQUA).append(entry.getValue())
                    .color(ChatColor.YELLOW).event(new HoverEvent(Action.SHOW_TEXT,
                            new ComponentBuilder("Click to Accept the Request!").color(ChatColor.GREEN).create()))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + entry.getValue())).create());
        }
        player.sendMessage(new ComponentBuilder(" ").create());
    }

    private static HashMap<UUID, String> getList(UUID uuid, int status) {
        List<UUID> uuids = new ArrayList<>();
        HashMap<UUID, String> map = new HashMap<>();
        try (Connection connection = SqlUtil.getConnection()) {
            switch (status) {
                case 0: {
                    PreparedStatement sql = connection.prepareStatement("SELECT sender FROM friends WHERE receiver=? AND status=0");
                    sql.setString(1, uuid.toString());
                    ResultSet result = sql.executeQuery();
                    while (result.next()) {
                        uuids.add(UUID.fromString(result.getString("sender")));
                    }
                    result.close();
                    sql.close();
                    break;
                }
                case 1: {
                    PreparedStatement sql = connection.prepareStatement("SELECT sender,receiver FROM friends WHERE (sender=? OR receiver=?) AND status=1");
                    sql.setString(1, uuid.toString());
                    sql.setString(2, uuid.toString());
                    ResultSet result = sql.executeQuery();
                    while (result.next()) {
                        if (result.getString("sender").equalsIgnoreCase(uuid.toString())) {
                            uuids.add(UUID.fromString(result.getString("receiver")));
                        } else {
                            uuids.add(UUID.fromString(result.getString("sender")));
                        }
                    }
                    break;
                }
            }
            if (uuids.isEmpty()) {
                return map;
            }
            String query = "SELECT username,uuid FROM player_data WHERE uuid=";
            for (int i = 0; i < uuids.size(); i++) {
                if (i >= (uuids.size() - 1)) {
                    query += "?";
                } else {
                    query += "? or uuid=";
                }
            }
            PreparedStatement sql2 = connection.prepareStatement(query);
            for (int i = 1; i < (uuids.size() + 1); i++) {
                sql2.setString(i, uuids.get(i - 1).toString());
            }
            ResultSet res2 = sql2.executeQuery();
            while (res2.next()) {
                map.put(UUID.fromString(res2.getString("uuid")), res2.getString("username"));
            }
            res2.close();
            sql2.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static HashMap<UUID, String> getFriendList(UUID uuid) {
        return getList(uuid, 1);
    }

    public static HashMap<UUID, String> getRequestList(UUID uuid) {
        return getList(uuid, 0);
    }

    public static void addFriend(Player player, String name) {
        if (name.equalsIgnoreCase(player.getName())) {
            player.sendMessage(new ComponentBuilder("You can't be your own friend, sorry!").color(ChatColor.RED).create());
            return;
        }
        HashMap<UUID, String> friendList = player.getFriends();
        for (String s : friendList.values()) {
            if (s.equalsIgnoreCase(name)) {
                player.sendMessage(new ComponentBuilder("That player is already on your Friend List!").color(ChatColor.RED).create());
                return;
            }
        }
        Player tp = MagicBungee.getPlayer(name);
        if (tp == null) {
            try {
                UUID tuuid = UUID.fromString(SqlUtil.uuidFromUsername(name));
                HashMap<UUID, String> requests = getList(tuuid, 0);
                if (requests.containsKey(player.getUniqueId())) {
                    player.sendMessage(new ComponentBuilder("You have already sent this player a Friend Request!")
                            .color(ChatColor.RED).create());
                    return;
                }
                if (player.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                    if (hasFriendsToggledOff(tuuid)) {
                        player.sendMessage(new ComponentBuilder("That player has Friend Requests toggled off!")
                                .color(ChatColor.RED).create());
                        return;
                    }
                }
                player.sendMessage(new ComponentBuilder("You have sent ").color(ChatColor.YELLOW).append(name)
                        .color(ChatColor.AQUA).append(" a Friend Request!").color(ChatColor.YELLOW).create());
                /**
                 * Add request to database
                 */
                try (Connection connection = SqlUtil.getConnection()) {
                    PreparedStatement sql = connection.prepareStatement("INSERT INTO friends (sender,receiver) VALUES (?,?)");
                    sql.setString(1, player.getUniqueId().toString());
                    sql.setString(2, tuuid.toString());
                    sql.execute();
                    sql.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                ActivityUtil.logActivity(player.getUniqueId(), "Send Friend Request", name);
            } catch (Exception ignored) {
                player.sendMessage(new ComponentBuilder("That player could not be found!").color(ChatColor.RED).create());
            }
            return;
        }
        if (tp.getRequests().containsKey(player.getUniqueId())) {
            player.sendMessage(new ComponentBuilder("You have already sent this player a Friend Request!")
                    .color(ChatColor.RED).create());
            return;
        }
        if (player.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
            if (tp.hasFriendToggledOff()) {
                player.sendMessage(new ComponentBuilder("That player has Friend Requests toggled off!")
                        .color(ChatColor.RED).create());
                return;
            }
        }
        tp.getRequests().put(player.getUniqueId(), player.getName());
        player.sendMessage(new ComponentBuilder("You have sent ").color(ChatColor.YELLOW).append(tp.getName())
                .color(ChatColor.AQUA).append(" a Friend Request!").color(ChatColor.YELLOW).create());
        tp.getRequests().put(player.getUniqueId(), player.getName());
        tp.sendMessage(new ComponentBuilder("\n" + player.getName()).color(ChatColor.GREEN)
                .append(" has sent you a Friend Request!").color(ChatColor.YELLOW).create());
        tp.sendMessage(new ComponentBuilder("Click to Accept").color(ChatColor.GREEN).event(new
                ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + player.getName())).append(" or ",
                ComponentBuilder.FormatRetention.NONE).color(ChatColor.AQUA).append("Click to Deny\n").color(ChatColor.RED)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend deny " + player.getName())).create());
        /**
         * Add request to database
         */
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO friends (sender,receiver) VALUES (?,?)");
            sql.setString(1, player.getUniqueId().toString());
            sql.setString(2, tp.getUniqueId().toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ActivityUtil.logActivity(player.getUniqueId(), "Send Friend Request", name);
    }

    public static void removeFriend(Player player, String name) {
        Player tp = MagicBungee.getPlayer(name);
        if (tp == null) {
            try {
                UUID tuuid = UUID.fromString(SqlUtil.uuidFromUsername(name));
                if (!player.getFriends().containsKey(tuuid)) {
                    player.sendMessage(new ComponentBuilder("That player isn't on your Friend List!").color(ChatColor.RED)
                            .create());
                    return;
                }
                player.getFriends().remove(tuuid);
                player.sendMessage(new ComponentBuilder("You removed ").color(ChatColor.RED).append(name)
                        .color(ChatColor.GREEN).append(" from your Friend List!").color(ChatColor.RED).create());
                try (Connection connection = SqlUtil.getConnection()) {
                    PreparedStatement sql = connection.prepareStatement("DELETE FROM friends WHERE (sender=? OR receiver=?) AND (sender=? OR receiver=?)");
                    sql.setString(1, player.getUniqueId().toString());
                    sql.setString(2, player.getUniqueId().toString());
                    sql.setString(3, tuuid.toString());
                    sql.setString(4, tuuid.toString());
                    sql.execute();
                    sql.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } catch (Exception ignored) {
                player.sendMessage(new ComponentBuilder("That player could not be found!").color(ChatColor.RED).create());
            }
            ActivityUtil.logActivity(player.getUniqueId(), "Remove Friend", name);
            return;
        }
        if (!player.getFriends().containsKey(tp.getUniqueId())) {
            player.sendMessage(new ComponentBuilder("That player isn't on your Friend List!").color(ChatColor.RED)
                    .create());
            return;
        }
        player.getFriends().remove(tp.getUniqueId());
        tp.getFriends().remove(player.getUniqueId());
        player.sendMessage(new ComponentBuilder("You removed ").color(ChatColor.RED).append(tp.getName())
                .color(ChatColor.GREEN).append(" from your Friend List!").color(ChatColor.RED).create());
        tp.sendMessage(new ComponentBuilder(player.getName()).color(ChatColor.GREEN)
                .append(" removed you from their Friend List!").color(ChatColor.RED).create());
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("DELETE FROM friends WHERE (sender=? OR receiver=?) AND (sender=? OR receiver=?)");
            sql.setString(1, player.getUniqueId().toString());
            sql.setString(2, player.getUniqueId().toString());
            sql.setString(3, tp.getUniqueId().toString());
            sql.setString(4, tp.getUniqueId().toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ActivityUtil.logActivity(player.getUniqueId(), "Remove Friend", name);
    }

    public static void acceptFriend(Player player, String name) {
        HashMap<UUID, String> requestList = player.getRequests();
        UUID tuuid = null;
        for (Map.Entry<UUID, String> entry : requestList.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(name)) {
                tuuid = entry.getKey();
                break;
            }
        }
        if (tuuid == null) {
            player.sendMessage(new ComponentBuilder("That player hasn't sent you a friend request!").color(ChatColor.RED).create());
            return;
        }
        player.getRequests().remove(tuuid);
        player.getFriends().put(tuuid, name);
        player.sendMessage(new ComponentBuilder("You have accepted ").color(ChatColor.YELLOW).append(name + "'s ")
                .color(ChatColor.GREEN).append("Friend Request!").color(ChatColor.YELLOW).create());
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE friends SET status=1 WHERE (sender=? OR receiver=?) AND (sender=? OR receiver=?)");
            sql.setString(1, player.getUniqueId().toString());
            sql.setString(2, player.getUniqueId().toString());
            sql.setString(3, tuuid.toString());
            sql.setString(4, tuuid.toString());
            sql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Player tp = MagicBungee.getPlayer(tuuid);
        if (tp != null) {
            tp.getFriends().put(player.getUniqueId(), player.getName());
            tp.sendMessage(new ComponentBuilder(player.getName()).color(player.getRank().getTagColor())
                    .append(" has accepted your Friend Request!").color(ChatColor.YELLOW).create());
        }
        ActivityUtil.logActivity(player.getUniqueId(), "Accept Friend Request", name);
    }

    public static void denyFriend(Player player, String name) {
        HashMap<UUID, String> requestList = player.getRequests();
        UUID tuuid = null;
        for (Map.Entry<UUID, String> entry : requestList.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(name)) {
                tuuid = entry.getKey();
                break;
            }
        }
        if (tuuid == null) {
            player.sendMessage(new ComponentBuilder("That player hasn't sent you a friend request!").color(ChatColor.RED).create());
            return;
        }
        player.getRequests().remove(tuuid);
        player.sendMessage(new ComponentBuilder("You have denied ").color(ChatColor.RED).append(name + "'s ")
                .color(ChatColor.GREEN).append("Friend Request!").color(ChatColor.RED).create());
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("DELETE FROM friends WHERE (sender=? OR receiver=?) AND (sender=? OR receiver=?)");
            sql.setString(1, player.getUniqueId().toString());
            sql.setString(2, player.getUniqueId().toString());
            sql.setString(3, tuuid.toString());
            sql.setString(4, tuuid.toString());
            sql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ActivityUtil.logActivity(player.getUniqueId(), "Denied Friend Request", name);
    }

    public static boolean hasFriendsToggledOff(UUID uuid) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM player_data WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            result.next();
            boolean toggledOff = result.getInt("toggled") == 1;
            result.close();
            sql.close();
            return toggledOff;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void helpMenu(Player player) {
        HoverEvent clickForEx = new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN +
                "Click for an example!").create());
        TextComponent help = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/friend help " +
                ChatColor.YELLOW + "- Shows this help menu");
        TextComponent list = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/friend list [Page] " +
                ChatColor.YELLOW + "- Lists all of your friends");
        TextComponent tp = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/friend tp [player] " +
                ChatColor.YELLOW + "- Brings you to your friend's server");
        TextComponent toggle = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/friend toggle " +
                ChatColor.YELLOW + "- Toggles friend requests");
        TextComponent add = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/friend add [player] " +
                ChatColor.YELLOW + "- Asks a player to be your friend");
        TextComponent remove = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/friend remove [player] " +
                ChatColor.YELLOW + "- Removes a player as your friend");
        TextComponent accept = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/friend accept [player] " +
                ChatColor.YELLOW + "- Accepts someone's friend request");
        TextComponent deny = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/friend deny [player] " +
                ChatColor.YELLOW + "- Denies someone's friend request");
        TextComponent requests = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/friend requests " +
                ChatColor.YELLOW + "- Lists all of your friend requests");
        help.setHoverEvent(clickForEx);
        list.setHoverEvent(clickForEx);
        tp.setHoverEvent(clickForEx);
        toggle.setHoverEvent(clickForEx);
        add.setHoverEvent(clickForEx);
        remove.setHoverEvent(clickForEx);
        accept.setHoverEvent(clickForEx);
        deny.setHoverEvent(clickForEx);
        requests.setHoverEvent(clickForEx);
        help.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/friend help"));
        list.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/friend list"));
        tp.setClickEvent(new ClickEvent(
                ClickEvent.Action.SUGGEST_COMMAND,
                "/friend tp "));
        toggle.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/friend toggle"));
        add.setClickEvent(new ClickEvent(
                ClickEvent.Action.SUGGEST_COMMAND,
                "/friend add "));
        remove.setClickEvent(new ClickEvent(
                ClickEvent.Action.SUGGEST_COMMAND,
                "/friend remove "));
        accept.setClickEvent(new ClickEvent(
                ClickEvent.Action.SUGGEST_COMMAND,
                "/friend accept "));
        deny.setClickEvent(new ClickEvent(
                ClickEvent.Action.SUGGEST_COMMAND,
                "/friend deny "));
        requests.setClickEvent(new ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/friend requests"));
        player.sendMessage(new TextComponent(ChatColor.YELLOW
                + "Friend Help Menu: " + ChatColor.GRAY + "(Click for Example)"));
        player.sendMessage(help);
        player.sendMessage(list);
        player.sendMessage(tp);
        player.sendMessage(toggle);
        player.sendMessage(add);
        player.sendMessage(remove);
        player.sendMessage(accept);
        player.sendMessage(deny);
        player.sendMessage(requests);
    }
}