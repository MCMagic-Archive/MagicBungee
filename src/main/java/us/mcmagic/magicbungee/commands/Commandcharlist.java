package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Commandcharlist extends MagicCommand {

    public Commandcharlist() {
        super(Rank.CHARACTERGUEST);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        ArrayList<Player> characters = new ArrayList<>();
        for (Player player : MagicBungee.getOnlinePlayers()) {
            if (player.getRank().name().toLowerCase().contains("character")) {
                characters.add(player);
            }
        }
        ConcurrentHashMap<ServerInfo, List<Player>> map = new ConcurrentHashMap<>();
        for (Player player : characters) {
            if (player == null || player.getServer() == null || player.getServer().getInfo() == null) {
                continue;
            }
            ServerInfo server = player.getServer().getInfo();
            if (!map.containsKey(server)) {
                map.put(server, new ArrayList<>(Arrays.asList(player)));
            } else {
                map.get(server).add(player);
            }
        }
        List<BaseComponent[]> msgs = new ArrayList<>();
        for (Map.Entry<ServerInfo, List<Player>> entry : map.entrySet()) {
            ServerInfo info = entry.getKey();
            ComponentBuilder msg = new ComponentBuilder(info.getName() + ": ").color(ChatColor.GREEN);
            List<Player> list = new ArrayList<>(entry.getValue());
            for (int i = 0; i < list.size(); i++) {
                Player tp = list.get(i);
                msg.append(tp.getName(), ComponentBuilder.FormatRetention.NONE).color(tp.getRank().getTagColor());
                if (i < (list.size() - 1)) {
                    msg.append(", ");
                }
            }
            msgs.add(msg.create());
        }
        sender.sendMessage(new ComponentBuilder("Online Characters: ").color(ChatColor.BLUE).create());
        if (msgs.isEmpty()) {
            sender.sendMessage(new ComponentBuilder("No Characters are online right now!").color(ChatColor.RED).create());
            return;
        }
        for (BaseComponent[] msg : msgs) {
            sender.sendMessage(msg);
        }
    }
}