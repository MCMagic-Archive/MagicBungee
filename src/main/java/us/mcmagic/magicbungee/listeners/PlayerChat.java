package us.mcmagic.magicbungee.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.Mute;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.socket.packets.dashboard.PacketChatMessage;
import us.mcmagic.magicbungee.utils.ChatUtil;
import us.mcmagic.magicbungee.utils.DateUtil;
import us.mcmagic.magicbungee.utils.MuteUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerChat implements Listener {
    public static HashMap<String, Boolean> mutedServers = new HashMap<>();
    private static HashMap<UUID, Long> time = new HashMap<>();
    private static HashMap<UUID, String> messageCache = new HashMap<>();
    private static int chatDelay = 2000;
    private static HashMap<Character, ChatColor> chars = new HashMap<>();
    private static List<String> whitelist = Arrays.asList("mcmagic.us", "magicaldreams.us", "craftventure.net",
            "minedisney.com", "discoveryridgeresort.com", "nauticalcraft.mcph.co", "mcprohosting.com");
    private static List<String> allowedChars = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
            "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "-", "=", "_", "+", "[", "]", "{",
            "}", "/", "\\", "?", "|", ",", ".", "<", ">", "`", "~", ";", ":", "'", "\"");
    static Pattern linkPattern = Pattern.compile("((\\d{1,3}\\.){3}\\d{1,3}(:\\d+)?)|(([0-9a-z:/]+(\\.|\\(dot\\)\\(\\.\\" +
            ")))+(aero|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|pro|tel|travel|ac|ad|ae|" +
            "af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|ax|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc" +
            "|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cu|cv|cx|cy|cz|cz|de|dj|dk|dm|do|dz|ec|ee|eg|er|es|et|eu|fi|fj|fk|fm|fo|f" +
            "r|ga|gb|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|im|in|io|iq|ir|is|it|" +
            "je|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|me|mg|mh|mk|ml|mn|mn" +
            "|mo|mp|mr|ms|mt|mu|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|nom|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|" +
            "pt|pw|py|qa|re|ra|rs|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sj|sk|sl|sm|sn|so|sr|st|su|sv|sy|sz|tc|td|tf|tg|th|tj" +
            "|tk|tl|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|ug|uk|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw|arpa)(:[0-" +
            "9]+)?((/([~0-9a-zA-Z#+%@./_-]+))?(/[0-9a-zA-Z+%@/&\\[\\];=_-]+)?)?)\\b");

    public PlayerChat() {
        chars.put('k', ChatColor.MAGIC);
        chars.put('l', ChatColor.BOLD);
        chars.put('m', ChatColor.STRIKETHROUGH);
        chars.put('n', ChatColor.UNDERLINE);
        chars.put('o', ChatColor.ITALIC);
        chars.put('r', ChatColor.RESET);
        MagicBungee.getProxyServer().getScheduler().schedule(MagicBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                messageCache.clear();
            }
        }, 0L, 5L, TimeUnit.MINUTES);
    }

    public static int getChatDelay() {
        return chatDelay;
    }

    public static void setChatDelay(int chatDelay) {
        PlayerChat.chatDelay = chatDelay;
    }

    @EventHandler
    public void onPlayerChat(ChatEvent event) {
        String m = "";
        String[] l = event.getMessage().split(" ");
        for (int i = 0; i < l.length; i++) {
            if (l[i].equals("") || l[i].equals(" ")) {
                continue;
            }
            if (i < (l.length - 1)) {
                m += l[i] + " ";
                continue;
            }
            m += l[i];
        }
        try {
            Player player;
            try {
                player = MagicBungee.getPlayer(((ProxiedPlayer) event.getSender()).getUniqueId());
            } catch (Exception e) {
                ((ProxiedPlayer) event.getSender())
                        .sendMessage(new ComponentBuilder("There was an error sending a chat message, please re-log!")
                                .color(ChatColor.RED).create());
                event.setCancelled(true);
                e.printStackTrace();
                return;
            }
            if (player == null) {
                ((ProxiedPlayer) event.getSender())
                        .sendMessage(new ComponentBuilder("There was an error sending a chat message, please re-log!")
                                .color(ChatColor.RED).create());
                event.setCancelled(true);
                return;
            }
            event.setMessage(player.getRank().getRankId() < Rank.EARNINGMYEARS.getRankId() ? removeCaps(player,
                    m.replace("sucks", "stinks")) : m.replace("sucks", "stinks"));
            if (player.getRank().getRankId() >= Rank.EARNINGMYEARS.getRankId()) {
                if (event.isCommand()) {
                    if (player.captchaMatches(event.getMessage())) {
                        player.captchaAccept();
                        player.sendMessage(new ComponentBuilder("Your AFK Timer has been reset!").color(ChatColor.GREEN)
                                .bold(true).create());
                        final UUID uuid = player.getUniqueId();
                        Player tp = MagicBungee.getPlayer(uuid);
                        Title title = MagicBungee.getProxyServer().createTitle();
                        title.fadeIn(10);
                        title.stay(100);
                        title.fadeOut(20);
                        title.title(new ComponentBuilder("Confirmed").color(ChatColor.RED).bold(true).create());
                        title.subTitle(new ComponentBuilder("Your AFK Timer has been reset!").color(ChatColor.RED)
                                .bold(true).create());
                        title.send(MagicBungee.getProxyServer().getPlayer(tp.getUniqueId()));
                        player.afkAction();
                        event.setCancelled(true);
                        return;
                    }
                    if (event.getMessage().startsWith("/afkcaptcha")) {
                        player.afkAction();
                        event.setCancelled(true);
                        return;
                    }
                }
                player.afkAction();
            }

            if (event.getMessage().toLowerCase().contains("minechat") ||
                    event.getMessage().toLowerCase().contains("minecraft connect")) {
                event.setCancelled(true);
                player.sendMessage(new ComponentBuilder("Your message containing 'MineChat' or 'Minecraft Connect' was blocked!")
                        .bold(true).color(ChatColor.RED).create());
                return;
            }
            if (player.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
                if (!event.isCommand()) {
                    if (messageCache.containsKey(player.getUniqueId())) {
                        if (event.getMessage().equalsIgnoreCase(messageCache.get(player.getUniqueId()))) {
                            player.sendMessage(new ComponentBuilder("Please do not repeat the same message!")
                                    .color(ChatColor.RED).bold(true).create());
                            event.setCancelled(true);
                            return;
                        }
                    }
                    messageCache.remove(player.getUniqueId());
                    messageCache.put(player.getUniqueId(), event.getMessage());
                    //ChatDelay Check
                    if (time.containsKey(player.getUniqueId()) && System.currentTimeMillis() <
                            time.get(player.getUniqueId())) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "You have to wait " + chatDelay / 1000 + " seconds before chatting!");
                        return;
                    }
                    time.remove(player.getUniqueId());
                    time.put(player.getUniqueId(), System.currentTimeMillis() + chatDelay);
                }
                if (event.isCommand()) {
                    String msg = event.getMessage();
                    if (msg.startsWith("/m ") || msg.startsWith("/msg ") || msg.startsWith("/r ")
                            || msg.startsWith("/reply ") || msg.startsWith("/tell ") || msg.startsWith("/whisper ")
                            || msg.startsWith("/w ") || msg.startsWith("/pchat ") || msg.startsWith("/shrug ")) {
                        if (time.containsKey(player.getUniqueId()) && System.currentTimeMillis() < time
                                .get(player.getUniqueId())) {
                            event.setCancelled(true);
                            player.sendMessage(ChatColor.RED + "You have to wait " + chatDelay / 1000 + " seconds before chatting!");
                            return;
                        }
                        time.remove(player.getUniqueId());
                        time.put(player.getUniqueId(), System.currentTimeMillis() + chatDelay);
                    }
                }
            }
            if (event.isCommand()) {
                if (MagicBungee.commandUtil.handleCommand((CommandSender) event.getSender(), event.getMessage().replaceFirst("/", ""))) {
                    event.setCancelled(true);
                    return;
                }
            }
            if (event.isCommand()) {
                return;
            }
            if (player.getRank().getRankId() < Rank.EARNINGMYEARS.getRankId()) {
                if (containsSwear(player, event.getMessage()) || isAdvert(player, event.getMessage())
                        || spamCheck(player, event.getMessage()) || containsUnicode(player, event.getMessage())) {
                    event.setCancelled(true);
                    return;
                }
                String mm = event.getMessage().toLowerCase().replace(".", "").replace("-", "").replace(",", "")
                        .replace("/", "").replace("_", "").replace(" ", "").replace(";", "");
                if (mm.contains("skype") || mm.contains(" skyp ") || mm.startsWith("skyp ") || mm.endsWith(" skyp") || mm.contains("skyp*")) {
                    event.setCancelled(true);
                    player.sendMessage(new ComponentBuilder("Please do not ask for Skype information!")
                            .color(ChatColor.RED).bold(true).create());
                    return;
                }
            }
            UUID uuid = player.getUniqueId();
            Mute mute = player.getMute();
            if (mute == null) {
                player.sendMessage(new ComponentBuilder("Please try chatting again in a moment. (Error Code 109)")
                        .color(ChatColor.RED).create());
                event.setCancelled(true);
                return;
            }
            if (mute.isMuted()) {
                long releaseTime = mute.getRelease();
                Date currentTime = new Date();
                if (currentTime.getTime() > releaseTime) {
                    MuteUtil.unmutePlayer(uuid);
                    player.getMute().setMuted(false);
                    event.setCancelled(false);
                } else {
                    String msg = ChatColor.RED + "You are silenced! You will be unsilenced in " +
                            DateUtil.formatDateDiff(mute.getRelease()) + ".";
                    if (!mute.getReason().equals("")) {
                        msg += " Reason: " + player.getMute().getReason();
                    }
                    player.sendMessage(msg);
                    event.setCancelled(true);
                    return;
                }
            }
            String serverName = player.getServer().getInfo().getName();
            if (!ChatUtil.isParkChat(serverName)) {
                if (player.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                    if (mutedServers.containsKey(serverName)) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "Chat is silenced right now!");
                        return;
                    }
                }
                if (!event.isCommand()) {
                    ChatUtil.logMessage(player.getUniqueId(), event.getMessage());
                }
                PacketChatMessage packet = new PacketChatMessage(player.getUniqueId(), player.getName(),
                        event.getMessage(), false, serverName, false);
                MagicBungee.socketConnection.sendMessage(packet);
                return;
            }
            if (player.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                if (mutedServers.containsKey("ParkChat")) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Chat is silenced right now!");
                    return;
                }
            }
            if (!event.isCancelled()) {
                serverName = serverName.replaceFirst("New", "");
                event.setCancelled(true);
                ComponentBuilder start = new ComponentBuilder("[").color(ChatColor.WHITE)
                        .append(serverName).color(ChatColor.GREEN).append("] ")
                        .color(ChatColor.WHITE).append("[").color(ChatColor.WHITE).append(player.getRank().getName())
                        .color(player.getRank().getTagColor()).append("] ").color(ChatColor.WHITE)
                        .append(player.getName() + ": ").color(ChatColor.GRAY).append("").color(player.getRank().getChatColor());
                if (player.getRank().getRankId() > Rank.EARNINGMYEARS.getRankId()) {
                    Object codes = translateCodes(player, event.getMessage(), start);
                    if (codes instanceof String) {
                        linkify(start, event.getMessage());
                        ComponentBuilder comp = start.color(player.getRank().getChatColor());
                        ChatUtil.parkChat(player, comp);
                    } else {
                        ComponentBuilder comp = (ComponentBuilder) codes;
                        ChatUtil.parkChat(player, comp);
                    }
                } else {
                    linkify(start, event.getMessage());
                    ComponentBuilder comp = start.color(player.getRank().getChatColor());
                    ChatUtil.parkChat(player, comp);
                }
                if (!event.isCommand()) {
                    ChatUtil.logMessage(player.getUniqueId(), event.getMessage());
                }
                PacketChatMessage packet = new PacketChatMessage(player.getUniqueId(), player.getName(),
                        event.getMessage(), true, serverName, false);
                MagicBungee.socketConnection.sendMessage(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ComponentBuilder linkify(ComponentBuilder start, String message) {
        Matcher matcher = linkPattern.matcher("");
        if (matcher.reset(message).find()) {
            int pos = 0;
            while (true) {
                String prev = matcher.group();
                if (!prev.startsWith("http://") && !prev.startsWith("https://")) {
                    prev = "http://" + prev;
                }
                start.append(message.substring(pos, matcher.start()));
                start.append(matcher.group()).event(new ClickEvent(ClickEvent.Action.OPEN_URL, prev))
                        .event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("Click to open " + prev)
                                .color(ChatColor.AQUA).create()));
                new TextComponent("");
                boolean find;
                start.append(message.substring(matcher.end(), (find = matcher.find()) ? matcher.start() : message.length()),
                        ComponentBuilder.FormatRetention.NONE);
                if (!find) {
                    break;
                }
                pos = matcher.end();
            }
        } else {
            return start.append(message);
        }
        return start;
    }

    public static String removeCaps(Player player, String msg) {
        int size = msg.toCharArray().length;
        if (size < 10) {
            return msg;
        }
        int amount = 0;
        for (char c : msg.toCharArray()) {
            if (Character.isUpperCase(c)) {
                amount++;
            }
        }
        if (Math.floor((double) (100 * (((float) amount) / size))) >= 50.0) {
            player.sendMessage(new ComponentBuilder("Please do not use a lot of capitals in your messages.")
                    .color(ChatColor.RED).bold(true).create());
            String s = "";
            for (int i = 0; i < msg.length(); i++) {
                if (i == 0) {
                    s += msg.charAt(0);
                    continue;
                }
                s += Character.toLowerCase(msg.charAt(i));
            }
            return s;
        }
        return msg;
    }


    public static boolean containsUnicode(Player player, String msg) {
        for (Character c : msg.toLowerCase().toCharArray()) {
            if (!allowedChars.contains(c)) {
                player.sendMessage(new ComponentBuilder("Your message contains blocked characters! ")
                        .color(ChatColor.RED).append("Read more about why your message was blocked.")
                        .color(ChatColor.AQUA).event(new HoverEvent(Action.SHOW_TEXT,
                                new ComponentBuilder("Click to open https://mcmagic.us/help/faq#blocked-chars!")
                                        .color(ChatColor.GREEN).create()))
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                "https://mcmagic.us/help/faq#blocked-chars")).create());
                return true;
            }
        }
        return false;
    }

    public static boolean spamCheck(Player player, String msg) {
        if (MagicBungee.getProxyServer().getPlayer(msg) != null) {
            return false;
        }
        Character last = null;
        int amount = 0;
        String word = "";
        boolean spam = false;
        for (char c : msg.toCharArray()) {
            if (last == null) {
                last = c;
                continue;
            }
            if (c == ' ') {
                if (MagicBungee.getProxyServer().getPlayer(word.trim()) != null) {
                    spam = false;
                }
                word = "";
                continue;
            }
            if (c == last) {
                amount++;
            } else {
                amount = 0;
            }
            if (amount >= 4) {
                spam = true;
            }
            last = c;
            word += c;
        }
        if (MagicBungee.getProxyServer().getPlayer(word.trim()) != null) {
            spam = false;
        }
        if (spam) {
            player.sendMessage(new ComponentBuilder("Please do not spam chat with excessive amounts of characters.")
                    .color(ChatColor.RED).bold(true).create());
            return true;
        }
        int numamount = 0;
        spam = false;
        word = "";
        for (char c : msg.toCharArray()) {
            if (c == ' ') {
                if (MagicBungee.getProxyServer().getPlayer(word.trim()) != null) {
                    spam = false;
                }
                word = "";
                continue;
            }
            if (isInt(c)) {
                numamount++;
            }
            if (numamount >= 8) {
                spam = true;
            }
            word += c;
        }
        if (MagicBungee.getProxyServer().getPlayer(word.trim()) != null) {
            spam = false;
        }
        if (spam) {
            player.sendMessage(new ComponentBuilder("Please do not spam chat with excessive amounts of numbers.")
                    .color(ChatColor.RED).bold(true).create());
            return true;
        }
        return false;
    }

    private static boolean isInt(char c) {
        try {
            Integer.parseInt(String.valueOf(c));
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean isAdvert(Player player, String msg) {
        Matcher m = linkPattern.matcher(msg.toLowerCase());
        if (m.find()) {
            if (isWhitelisted(m.toMatchResult().group())) {
                return false;
            }
            advertMessage(player.getName(), msg);
            player.sendMessage(new ComponentBuilder("Please do not attempt to advertise or share links. It is against the rules.")
                    .color(ChatColor.RED).bold(true).create());
            return true;
        }
        return false;
    }

    private static boolean isWhitelisted(String group) {
        for (String s : whitelist) {
            String m;
            if (group.startsWith("https://")) {
                m = group.replaceFirst("https://", "");
            } else if (group.startsWith("http://")) {
                m = group.replaceFirst("http://", "");
            } else {
                m = group;
            }
            if (m.startsWith(s) || m.endsWith(s)) {
                return true;
            }
        }
        return false;
    }

    private static void advertMessage(String name, String msg) {
        BaseComponent[] comp = new ComponentBuilder(name).color(ChatColor.AQUA)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/msg " + name + " Please do not advertise or try to share links."))
                .event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("Click to remind this player not to advertise!")
                        .color(ChatColor.GREEN).create())).append(" possibly advertises: ").color(ChatColor.RED)
                .append(msg).color(ChatColor.AQUA).create();
        for (Player player : MagicBungee.getOnlinePlayers()) {
            if (player.getRank().getRankId() >= Rank.CASTMEMBER.getRankId()) {
                player.sendMessage(comp);
            }
        }
    }

    public static Object translateCodes(Player player, String message, ComponentBuilder start) {
        boolean started = message.startsWith("&");
        String[] list = message.split("&");
        if (list.length == 1) {
            return message;
        }
        ComponentBuilder msg = new ComponentBuilder(start);
        for (int i = 0; i < list.length; i++) {
            String s = list[i];
            if (!started) {
                if (i > 0) {
                    char first = s.charAt(0);
                    msg.append(s.substring(1));
                    if (chars.containsKey(first)) {
                        switch (first) {
                            case 'k':
                                msg.obfuscated(true);
                                break;
                            case 'l':
                                msg.bold(true);
                                break;
                            case 'm':
                                msg.strikethrough(true);
                                break;
                            case 'n':
                                msg.underlined(true);
                                break;
                            case 'o':
                                msg.italic(true);
                                break;
                            case 'r':
                                msg.reset();
                                break;
                        }
                    } else {
                        msg.color(ChatColor.getByChar(first));
                    }
                } else {
                    msg.append(s);
                    msg.color(player.getRank().getChatColor());
                }
            } else {
                if (i > 0) {
                    char first = s.charAt(0);
                    msg.append(s.substring(1));
                    if (chars.containsKey(first)) {
                        switch (first) {
                            case 'k':
                                msg.obfuscated(true);
                                break;
                            case 'l':
                                msg.bold(true);
                                break;
                            case 'm':
                                msg.strikethrough(true);
                                break;
                            case 'n':
                                msg.underlined(true);
                                break;
                            case 'o':
                                msg.italic(true);
                                break;
                            case 'r':
                                msg.reset();
                                break;
                        }
                    } else {
                        msg.color(ChatColor.getByChar(first));
                    }
                }
            }
        }
        return msg;
    }

    private ChatColor getColor(char c) {
        if (chars.containsKey(c)) {
        }
        return ChatColor.RESET;
    }

    public static void playerLogout(UUID uuid) {
        time.remove(uuid);
        messageCache.remove(uuid);
    }

    public static void swearMessage(String name, String msg) {
        BaseComponent[] comp = new ComponentBuilder(name).color(ChatColor.AQUA)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/msg " + name + " Please keep chat appropriate."))
                .event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("Click to ask this user to be nice!")
                        .color(ChatColor.GREEN).create())).append(" swears: ").color(ChatColor.RED)
                .append(msg).color(ChatColor.AQUA).create();
        for (Player player : MagicBungee.getOnlinePlayers()) {
            if (player.getRank().getRankId() >= Rank.CASTMEMBER.getRankId()) {
                player.sendMessage(comp);
            }
        }
    }

    public static boolean containsSwear(Player player, String msg) {
        boolean bool = false;
        final String omsg = msg.replace(".", "").replace("-", "")
                .replace(",", "").replace("/", "").replace("()", "o")
                .replace("0", "o").replace("_", "").replace("@", "a")
                .replace("$", "s").replace(";", "");
        final String m = msg.replace(" ", "").replace(".", "")
                .replace("-", "").replace(",", "").replace("/", "")
                .replace("()", "o").replace("0", "o").replace("_", "")
                .replace("@", "a").replace("$", "s").replace(";", "");
        List<String> swearList = MagicBungee.swearList;
        List<String> specificList = MagicBungee.swearListSpecific;
        for (String s : swearList) {
            if (m.toLowerCase().contains(s)) {
                bool = true;
                break;
            }
        }
        if (omsg.equalsIgnoreCase("ass") || omsg.toLowerCase().startsWith("ass ") || omsg.toLowerCase().endsWith(" ass")
                || omsg.contains(" ass ")) {
            bool = true;
        }
        if (!bool) {
            for (String s : specificList) {
                if (omsg.toLowerCase().contains(s)) {
                    bool = true;
                    break;
                }
            }
        }
        if (bool) {
            player.sendMessage(new ComponentBuilder("Please do not swear!").color(ChatColor.RED).bold(true).create());
            ChatUtil.logMessage(player.getUniqueId(), msg);
            swearMessage(player.getName(), msg);
            return true;
        }
        return false;
    }
}
