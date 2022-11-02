package us.mcmagic.magicbungee.listeners;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Marc on 7/3/15
 */
public class TabComplete implements Listener {

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        if (!event.getCursor().startsWith("/")) {
            return;
        }
        String[] split = Pattern.compile(" ").split(event.getCursor().substring(1));
        String command = split[0].toLowerCase();
        String[] args = Arrays.copyOfRange(split, 1, split.length);
        TreeMap<String, MagicCommand> commands = MagicBungee.commandUtil.getCommands();
        for (Map.Entry<String, MagicCommand> entry : commands.entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(command) && !entry.getValue().getAliases().contains(command)) {
                continue;
            }
            Iterable<String> l = entry.getValue().onTabComplete((CommandSender) event.getSender(), args);
            List<String> list = new ArrayList<>();
            for (String s : l) {
                list.add(s);
            }
            if (!list.isEmpty()) {
                event.getSuggestions().clear();
                for (String s : list) {
                    event.getSuggestions().add(s);
                }
            }
        }
    }
}