package us.mcmagic.magicbungee.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.commands.*;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by Marc on 7/2/15
 */
public class CommandUtil {
    private HashMap<String, MagicCommand> commands = new HashMap<>();

    public CommandUtil() {
        initialize();
    }

    public void initialize() {
        commands.clear();
        register("apply", new Commandapply());
        register("audio", new Commandaudio());
        register("b", new Commandb());
        register("ban", new Commandban());
        register("banip", new Commandbanip());
        register("bseen", new Commandbseen());
        register("bug", new Commandbug());
        register("cache", new Commandcache());
        register("cc", new Commandcc());
        register("charlist", new Commandcharlist());
        register("chatdelay", new Commandchatdelay());
        register("chatreload", new Commandchatreload());
        register("cmds", new Commandcmds());
        register("disconnectall", new Commanddisconnectall());
        register("end", new Commandend());
        register("find", new Commandfind());
        register("friend", new Commandfriend());
        register("help", new Commandhelp());
        register("ho", new Commandho());
        register("ip", new Commandip());
        register("ipseen", new Commandipseen());
        register("join", new Commandjoin());
        register("kick", new Commandkick());
        register("maintenance", new Commandmaintenance());
        register("mentions", new Commandmentions());
        register("modlog", new Commandmodlog());
        register("motdrl", new Commandmotdrl());
        register("msg", new Commandmsg());
        register("msgtoggle", new Commandmsgtoggle());
        register("mumble", new Commandmumble());
        register("mute", new Commandmute());
        register("mutechat", new Commandmutechat());
        register("namecheck", new Commandnamecheck());
        register("oc", new Commandoc());
        register("parties", new Commandparties());
        register("party", new Commandparty());
        register("pchat", new Commandpchat());
        register("reboot", new Commandreboot());
        register("reloadwords", new Commandreloadwords());
        register("reply", new Commandreply());
        register("rp", new Commandrp());
        register("sc", new Commandsc());
        register("send", new Commandsend());
        register("server", new Commandserver());
        register("social", new Commandsocial());
        register("stafflist", new Commandstafflist());
        register("store", new Commandstore());
        register("tempban", new Commandtempban());
        register("unban", new Commandunban());
        register("unbanip", new Commandunbanip());
        register("unmute", new Commandunmute());
        register("uptime", new Commanduptime());
        register("vote", new Commandvote());
        register("whereami", new Commandwhereami());
    }

    public boolean handleCommand(CommandSender sender, String message) {
        try {
            String[] parts = message.split(" ");
            String command = parts[0].toLowerCase();
            String[] args = new String[parts.length - 1];
            int i = 0;
            boolean first = true;
            for (String s : parts) {
                if (first) {
                    first = false;
                    continue;
                }
                args[i] = s;
                i++;
            }
            if (!commands.containsKey(command)) {
                for (MagicCommand c : new ArrayList<>(commands.values())) {
                    if (c.getAliases().contains(command)) {
                        execute(sender, c, command, args);
                        return true;
                    }
                }
                return false;
            }
            execute(sender, commands.get(command), command, args);
            return true;
        } catch (Exception e) {
            sender.sendMessage(new ComponentBuilder("An internal error occured whilst executing this command.")
                    .color(ChatColor.RED).create());
            e.printStackTrace();
            return true;
        }
    }

    public TreeMap<String, MagicCommand> getCommands() {
        return new TreeMap<>(commands);
    }

    public MagicCommand getCommand(String label) {
        return commands.get(label);
    }

    private void execute(CommandSender sender, MagicCommand c, String command, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
            if (!c.canPerformCommand(player.getRank())) {
                player.sendMessage(new ComponentBuilder("You do not have permission to execute this command!")
                        .color(ChatColor.RED).create());
                return;
            }
            c.execute(sender, command, args);
            return;
        }
        c.execute(sender, command, args);
    }

    public void register(String label, MagicCommand command) {
        if (commands.containsKey(label.toLowerCase())) {
            commands.remove(label.toLowerCase());
        }
        commands.put(label.toLowerCase(), command);
    }
}