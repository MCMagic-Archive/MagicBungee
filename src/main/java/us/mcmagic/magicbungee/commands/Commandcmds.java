package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.permissions.Rank;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Marc on 7/3/15
 */
public class Commandcmds extends MagicCommand {

    public Commandcmds() {
        super(Rank.DEVELOPER);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(new ComponentBuilder("Registered Commands:").color(ChatColor.GREEN).create());
        String msg = null;
        TreeMap<String, MagicCommand> map = MagicBungee.commandUtil.getCommands();
        for (Map.Entry<String, MagicCommand> entry : map.entrySet()) {
            if (msg != null) {
                msg += "\n";
            } else {
                msg = "";
            }
            msg += "- /" + entry.getKey() + " ";
            List<String> aliases = entry.getValue().getAliases();
            if (!aliases.isEmpty()) {
                msg += "(";
                for (int i = 0; i < aliases.size(); i++) {
                    msg += aliases.get(i);
                    if (i < (aliases.size() - 1)) {
                        msg += ", ";
                    }
                }
                msg += ") ";
            }
            msg += entry.getValue().getRank().getName();
        }
        sender.sendMessage(new ComponentBuilder(msg).color(ChatColor.YELLOW).create());
    }
}