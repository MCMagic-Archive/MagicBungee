package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Commandjoin extends MagicCommand {
    public List<String> servers = new ArrayList<>();

    public Commandjoin() {
        initialize();
    }

    public void initialize() {
        servers.clear();
        File serversfile = new File("plugins/MagicBungee/servers.yml");
        Configuration config;
        try {
            config = ConfigurationProvider.getProvider(
                    YamlConfiguration.class).load(serversfile);
        } catch (IOException e) {
            return;
        }
        List<String> list = new ArrayList<>();
        for (String s : config.getStringList("servers")) {
            list.add(s.toLowerCase());
        }
        servers = list;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        TextComponent top = new TextComponent(ChatColor.GREEN + "Here is a list of servers you can join: " +
                ChatColor.GRAY + "(Click to join)");
        if (args.length == 1) {
            if (getServers().contains(args[0].toLowerCase())) {
                try {
                    player.connect(MagicBungee.getProxyServer().getServerInfo(args[0]));
                } catch (Exception e) {
                    player.sendMessage(new TextComponent(ChatColor.RED + "There was a problem joining that server!"));
                }
                return;
            }
        }
        player.sendMessage(top);
        for (String servername : getServers()) {
            TextComponent txt = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + formatName(servername));
            txt.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN +
                    "Click to join the " + ChatColor.AQUA + formatName(servername) + ChatColor.GREEN + " server!").create()));
            txt.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/join " + servername));
            player.sendMessage(txt);
        }
    }

    private String formatName(String s) {
        String ns = "";
        if (s.length() < 4) {
            for (char c : s.toCharArray()) {
                ns += Character.toString(Character.toUpperCase(c));
            }
            return ns;
        }
        Character last = null;
        for (char c : s.toCharArray()) {
            if (last == null) {
                last = c;
                ns += Character.toString(Character.toUpperCase(c));
                continue;
            }
            if (Character.toString(last).equals(" ")) {
                ns += Character.toString(Character.toUpperCase(c));
            } else {
                ns += Character.toString(c);
            }
            last = c;
        }
        return ns;
    }

    public List<String> getServers() {
        return new ArrayList<>(servers);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (String s : getServers()) {
            list.add(s);
        }
        Collections.sort(list);
        if (args.length == 0) {
            return list;
        }
        List<String> l2 = new ArrayList<>();
        String arg = args[args.length - 1];
        for (String s : list) {
            if (s.toLowerCase().startsWith(arg.toLowerCase())) {
                l2.add(s);
            }
        }
        Collections.sort(l2);
        return l2;
    }
}