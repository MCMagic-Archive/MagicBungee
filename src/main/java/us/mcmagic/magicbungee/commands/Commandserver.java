package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Marc on 7/2/15
 */
public class Commandserver extends MagicCommand {

    public Commandserver() {
        super(Rank.CASTMEMBER);
        tabCompletePlayers = true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Only players can do this!").color(ChatColor.RED).create());
            return;
        }
        Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        if (args.length == 0) {
            player.sendMessage(new ComponentBuilder("You are currently on " + player.getServer().getInfo().getName())
                    .color(ChatColor.GREEN).create());
            String msg = "The following servers exist: ";
            Collection<ServerInfo> values = MagicBungee.getProxyServer().getServers().values();
            ServerInfo[] servers = values.toArray(new ServerInfo[values.size()]);
            for (int i = 0; i < servers.length; i++) {
                msg += servers[i].getName();
                if (i < (servers.length + 1)) {
                    msg += ", ";
                }
            }
            player.sendMessage(new ComponentBuilder(msg).color(ChatColor.GREEN).create());
            return;
        }
        ServerInfo server = MagicBungee.getProxyServer().getServerInfo(args[0]);
        if (server == null) {
            player.sendMessage(new ComponentBuilder("That server doesn't exist!").color(ChatColor.RED).create());
            return;
        }
        ((ProxiedPlayer) sender).connect(server);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (ServerInfo server : MagicBungee.getProxyServer().getServers().values()) {
            list.add(server.getName());
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
