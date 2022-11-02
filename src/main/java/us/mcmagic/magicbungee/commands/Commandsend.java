package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Marc on 7/2/15
 */
public class Commandsend extends MagicCommand {
    private BaseComponent[] usage = new ComponentBuilder("/send <player|all|current> <target>").color(ChatColor.RED).create();

    public Commandsend() {
        super(Rank.DEVELOPER);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            if (args.length != 2) {
                sender.sendMessage(usage);
                return;
            }
            ServerInfo server = MagicBungee.getProxyServer().getServerInfo(args[1]);
            if (server == null) {
                sender.sendMessage(new ComponentBuilder("The server '" + args[1] +
                        "' does not exist!").color(ChatColor.RED).create());
                return;
            }
            if (args[0].equalsIgnoreCase("all")) {
                sender.sendMessage(new ComponentBuilder("Sending ").color(ChatColor.GREEN).append("all players")
                        .color(ChatColor.GOLD).append(" to ").color(ChatColor.GREEN)
                        .append(server.getName()).color(ChatColor.YELLOW).create());
                for (ProxiedPlayer tp : MagicBungee.getProxyServer().getPlayers()) {
                    tp.connect(server);
                }
                return;
            }
            if (args[0].equalsIgnoreCase("current")) {
                sender.sendMessage(new ComponentBuilder("Only players can do this!").color(ChatColor.RED).create());
                return;
            }
            Player tp = MagicBungee.getPlayer(args[0]);
            if (tp == null) {
                sender.sendMessage(new ComponentBuilder("Player not found!").color(ChatColor.RED).create());
                return;
            }
            sender.sendMessage(new ComponentBuilder("Sending ").color(ChatColor.GREEN).append(tp.getName())
                    .color(ChatColor.GOLD).append(" to ").color(ChatColor.GREEN)
                    .append(server.getName()).color(ChatColor.YELLOW).create());
            MagicBungee.getProxyServer().getPlayer(tp.getUniqueId()).connect(server);
            return;
        }
        Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        if (args.length != 2) {
            sender.sendMessage(usage);
            return;
        }
        ServerInfo server = MagicBungee.getProxyServer().getServerInfo(args[1]);
        if (server == null) {
            player.sendMessage(new ComponentBuilder("The server '" + args[1] +
                    "' does not exist!").color(ChatColor.RED).create());
            return;
        }
        if (args[0].equalsIgnoreCase("all")) {
            player.sendMessage(new ComponentBuilder("Sending ").color(ChatColor.GREEN).append("all players")
                    .color(ChatColor.GOLD).append(" to ").color(ChatColor.GREEN)
                    .append(server.getName()).color(ChatColor.YELLOW).create());
            for (ProxiedPlayer tp : MagicBungee.getProxyServer().getPlayers()) {
                tp.connect(server);
            }
            return;
        }
        if (args[0].equalsIgnoreCase("current")) {
            String name = player.getServer().getInfo().getName();
            player.sendMessage(new ComponentBuilder("Sending ").color(ChatColor.GREEN).append("players on " +
                    server.getName() + " (" + player.getServer().getInfo().getPlayers().size() + " players)")
                    .color(ChatColor.GOLD).append(" to ").color(ChatColor.GREEN).append(server.getName())
                    .color(ChatColor.YELLOW).create());
            for (ProxiedPlayer tp : MagicBungee.getProxyServer().getPlayers()) {
                if (tp.getServer().getInfo().getName().equals(name)) {
                    tp.connect(server);
                }
            }
            return;
        }
        Player tp = MagicBungee.getPlayer(args[0]);
        if (tp == null) {
            player.sendMessage(new ComponentBuilder("Player not found!").color(ChatColor.RED).create());
            return;
        }
        player.sendMessage(new ComponentBuilder("Sending ").color(ChatColor.GREEN).append(tp.getName())
                .color(ChatColor.GOLD).append(" to ").color(ChatColor.GREEN)
                .append(server.getName()).color(ChatColor.YELLOW).create());
        MagicBungee.getProxyServer().getPlayer(tp.getUniqueId()).connect(server);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 0) {
            list.add("all");
            list.add("current");
            for (Player tp : MagicBungee.getOnlinePlayers()) {
                list.add(tp.getName());
            }
            Collections.sort(list);
            return list;
        } else if (args.length == 1) {
            list.add("all");
            list.add("current");
            for (Player tp : MagicBungee.getOnlinePlayers()) {
                list.add(tp.getName());
            }
            String arg = args[0];
            List<String> l2 = new ArrayList<>();
            for (String s : list) {
                if (s.toLowerCase().startsWith(arg.toLowerCase())) {
                    l2.add(s);
                }
            }
            Collections.sort(l2);
            return l2;
        } else if (args.length == 2) {
            for (ServerInfo server : MagicBungee.getProxyServer().getServers().values()) {
                list.add(server.getName());
            }
            String arg = args[1];
            List<String> l2 = new ArrayList<>();
            for (String s : list) {
                if (s.toLowerCase().startsWith(arg.toLowerCase())) {
                    l2.add(s);
                }
            }
            Collections.sort(l2);
            return l2;
        }
        return list;
    }
}