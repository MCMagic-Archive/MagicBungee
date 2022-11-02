package us.mcmagic.magicbungee.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.commands.Commandjoin;
import us.mcmagic.magicbungee.handlers.Party;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ChatUtil implements Listener {
    public static HashMap<UUID, UUID> msgReply = new HashMap<>();
    private static List<String> parkChatServers = new ArrayList<>();
    private static HashMap<UUID, List<String>> messages = new HashMap<>();

    public ChatUtil() {
        initialize();
        MagicBungee.getProxyServer().getScheduler().schedule(MagicBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                try (Connection connection = SqlUtil.getConnection()) {
                    if (messages.isEmpty()) {
                        return;
                    }
                    int amount = 0;
                    for (Map.Entry<UUID, List<String>> entry : new HashSet<>(messages.entrySet())) {
                        for (String s : entry.getValue()) {
                            amount++;
                        }
                    }
                    String statement = "INSERT INTO chat (user, message) VALUES ";
                    int i = 0;
                    HashMap<Integer, String> lastList = new HashMap<>();
                    for (Map.Entry<UUID, List<String>> entry : new HashSet<>(messages.entrySet())) {
                        if (entry == null || entry.getKey() == null || messages == null) {
                            continue;
                        }
                        for (String s : new ArrayList<>(messages.remove(entry.getKey()))) {
                            statement += "(?, ?)";
                            if (((i / 2) + 1) < amount) {
                                statement += ", ";
                            }
                            lastList.put(i += 1, entry.getKey().toString());
                            lastList.put(i += 1, s);
                        }
                    }
                    statement += ";";
                    PreparedStatement sql = connection.prepareStatement(statement);
                    for (Map.Entry<Integer, String> entry : new HashSet<>(lastList.entrySet())) {
                        sql.setString(entry.getKey(), entry.getValue());
                    }
                    sql.execute();
                    sql.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 5, 5, TimeUnit.SECONDS).getId();
    }

    public static void logMessage(UUID uuid, String message) {
        if (messages.containsKey(uuid)) {
            List<String> list = new ArrayList<>(messages.get(uuid));
            list.add(message);
            messages.put(uuid, list);
            return;
        }
        messages.put(uuid, Collections.singletonList(message));
    }

    public static void initialize() {
        File file = new File("plugins/MagicBungee/chat.yml");
        Configuration chat = null;
        try {
            chat = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String s : chat.getStringList("connected-servers")) {
            parkChatServers.add(s);
        }
    }

    public static void reloadServers() {
        parkChatServers.clear();
        initialize();
        ((Commandjoin) MagicBungee.commandUtil.getCommand("join")).initialize();
    }

    public static List<String> getParkChatServers() {
        return new ArrayList<>(parkChatServers);
    }

    public static void parkChat(Player player, ComponentBuilder comp) {
        sendParkChat(player, comp);
    }

    private static void sendParkChat(Player player, ComponentBuilder comp) {
        final BaseComponent[] base = comp.create();
        for (Player tp : MagicBungee.getOnlinePlayers()) {
            if (tp.getServer() == null || tp.getServer().getInfo() == null || tp.getServer().getInfo().getName() == null) {
                continue;
            }
            if (isParkChat(tp.getServer().getInfo().getName())) {
                BaseComponent[] send = base;
                BaseComponent[] mentionText = null;
                boolean mention = false;
                if (tp.hasMentions() && !tp.getUniqueId().equals(player.getUniqueId())) {
                    for (int i = 7; i < send.length; i++) {
                        BaseComponent tempComp = send[i];
                        String possibleMention = tempComp.toPlainText().toLowerCase();
                        String name = tp.getName().toLowerCase();
                        if (possibleMention.contains(" " + name + " ") || possibleMention.startsWith(name + " ") ||
                                possibleMention.endsWith(" " + name) || possibleMention.equalsIgnoreCase(name) ||
                                possibleMention.contains(" " + name + ".") || possibleMention.startsWith(name + ".") ||
                                possibleMention.contains(" " + name + "!") || possibleMention.startsWith(name + "!")) {
                            mention = true;
                            BaseComponent add = new ComponentBuilder("* ").color(ChatColor.BLUE).create()[0];
                            mentionText = new BaseComponent[send.length + 1];
                            mentionText[0] = add;
                            for (int i2 = 0; i2 < send.length; i2++) {
                                mentionText[i2 + 1] = send[i2];
                            }
                            break;
                        }
                    }
                }
                if (mention) {
                    tp.sendMessage(mentionText);
                    mentionSound(tp);
                } else {
                    tp.sendMessage(send);
                }
            }
        }
    }

    public static void mentionSound(Player tp) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("MentionSound");
            MagicBungee.getProxyServer().getPlayer(tp.getUniqueId()).getServer().sendData("BungeeCord", b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getString(BaseComponent[] message) {
        String string = "";
        for (BaseComponent b : message) {
            string += b.toPlainText();
        }
        return string;
    }

    public static void parkChat(Player player, String message) {
        ServerInfo server = player.getServer().getInfo();
        ComponentBuilder msg = new ComponentBuilder("[").color(ChatColor.WHITE).append(server.getName())
                .color(ChatColor.GREEN).append("] ").color(ChatColor.WHITE).append("[").color(ChatColor.WHITE)
                .append(player.getRank().getName()).color(player.getRank().getTagColor()).append("] ")
                .color(ChatColor.WHITE).append(player.getName() + ": ").color(ChatColor.GRAY).append(message)
                .color(player.getRank().getChatColor());
        sendParkChat(player, msg);
    }

    public static void socialSpyMessage(Player from, Player to, String message, String command) {
        if (isParkChat(from.getServer().getInfo().getName())) {
            BaseComponent[] msg = new ComponentBuilder(from.getName()).color(ChatColor.WHITE).append(": /" +
                    command + " " + to.getName() + " " + message).color(ChatColor.WHITE).create();
            for (Player tp : MagicBungee.getOnlinePlayers()) {
                if (tp.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                    continue;
                }
                if (tp.getServer() == null) {
                    continue;
                }
                if (tp.getUniqueId().equals(from.getUniqueId()) || tp.getUniqueId().equals(to.getUniqueId())) {
                    continue;
                }
                if (isParkChat(tp.getServer().getInfo().getName())) {
                    tp.sendMessage(msg);
                }
            }
        } else {
            String server = from.getServer().getInfo().getName();
            for (Player tp : MagicBungee.getOnlinePlayers()) {
                if (tp.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                    continue;
                }
                if (tp.getServer() == null) {
                    continue;
                }
                if (tp.getUniqueId().equals(from.getUniqueId()) || tp.getUniqueId().equals(to.getUniqueId())) {
                    continue;
                }
                if (tp.getServer().getInfo().getName().equals(server)) {
                    tp.sendMessage(from.getName() + ": /" + command + " " + to.getName() + " " + message);
                }
            }
        }
    }

    public static void socialSpyParty(Player player, Party party, String message, String command) {
        if (isParkChat(player.getServer().getInfo().getName())) {
            BaseComponent[] msg = new ComponentBuilder(player.getName()).color(ChatColor.WHITE).append(": /" +
                    command + " " + party.getLeader().getName() + " " + message).color(ChatColor.WHITE).create();
            for (Player tp : MagicBungee.getOnlinePlayers()) {
                if (tp.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                    continue;
                }
                if (tp.getServer() == null) {
                    continue;
                }
                if (party.getMembers().contains(tp.getUniqueId())) {
                    continue;
                }
                if (isParkChat(tp.getServer().getInfo().getName())) {
                    tp.sendMessage(msg);
                }
            }
        } else {
            String server = player.getServer().getInfo().getName();
            for (Player tp : MagicBungee.getOnlinePlayers()) {
                if (tp.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                    continue;
                }
                if (tp.getServer() == null) {
                    continue;
                }
                if (party.getMembers().contains(tp.getUniqueId())) {
                    continue;
                }
                if (tp.getServer().getInfo().getName().equals(server)) {
                    tp.sendMessage(player.getName() + ": /" + command + " " + party.getLeader().getName() + " " + message);
                }
            }
        }
    }

    public static boolean isParkChat(String name) {
        return parkChatServers.contains(name);
    }

    public static void staffChatMessage(BaseComponent[] message) {
        for (Player player : MagicBungee.getOnlinePlayers()) {
            if (player.getRank().getRankId() >= Rank.EARNINGMYEARS.getRankId()) {
                try {
                    player.sendMessage(message);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void staffChatMessage(String message) {
        for (Player player : MagicBungee.getOnlinePlayers()) {
            if (player.getRank().getRankId() >= Rank.EARNINGMYEARS.getRankId()) {
                try {
                    player.sendMessage(message);
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static String stripCaps(String msg) {
        int[] caps = checkCaps(msg);
        if (percentageCaps(caps) >= 50 || checkCapsInRow(caps) >= 5) {
            String[] parts = msg.split(" ");
            boolean capsAllowed = false;
            for (int i = 0; i < parts.length; i++) {
                boolean isOnWhitelist = false;
                for (String whitelist : new ArrayList<String>()) {
                    if (whitelist.equalsIgnoreCase(parts[i])) {
                        isOnWhitelist = true;
                        capsAllowed = true;
                        break;
                    }
                }

                if (!isOnWhitelist) {
                    if (!capsAllowed) {
                        char firstChar = parts[i].charAt(0);
                        parts[i] = (firstChar + parts[i].toLowerCase()
                                .substring(1));
                    } else {
                        parts[i] = parts[i].toLowerCase();
                    }

                    capsAllowed = (!parts[i].endsWith("."))
                            && (!parts[i].endsWith("!"));
                }
            }

            return join(parts, " ");
        } else {
            return msg;
        }
    }

    private static String join(String[] list, String add) {
        String done = "";
        for (int i = 1; i < list.length; i++) {
            done += list[i] + add;
        }
        return done;
    }

    public static int[] checkCaps(String message) {
        int[] editedMsg = new int[message.length()];
        String[] parts = message.split(" ");
        for (int i = 0; i < parts.length; i++) {
            for (String whitelisted : new ArrayList<String>()) {
                if (whitelisted.equalsIgnoreCase(parts[i])) {
                    parts[i] = parts[i].toLowerCase();
                }
            }
        }

        String msg = join(parts, " ");

        for (int j = 0; j < msg.length(); j++) {
            if ((Character.isUpperCase(msg.charAt(j)))
                    && (Character.isLetter(msg.charAt(j))))
                editedMsg[j] = 1;
            else {
                editedMsg[j] = 0;
            }
        }
        return editedMsg;
    }

    public static int percentageCaps(int[] caps) {
        int sum = 0;
        for (int cap : caps) {
            sum += cap;
        }
        double ratioCaps = sum / caps.length;
        return (int) (100.0D * ratioCaps);
    }

    public static int checkCapsInRow(int[] caps) {
        int sum = 0;
        int sumTemp = 0;
        int j = caps.length;
        for (int i2 : caps) {
            if (i2 == 1) {
                sumTemp++;
                sum = Math.max(sum, sumTemp);
            } else {
                sumTemp = 0;
            }
        }
        return sum;
    }

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}