package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import us.mcmagic.magicbungee.handlers.AddressBan;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.BanUtil;
import us.mcmagic.magicbungee.utils.SqlUtil;

import java.util.List;

public class Commandipseen extends MagicCommand {

    public Commandipseen() {
        super(Rank.CASTMEMBER);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            AddressBan ban = BanUtil.getAddressBan(args[0]);
            if (ban != null) {
                sender.sendMessage(new ComponentBuilder("This IP Address is banned for ").color(ChatColor.RED)
                        .append(ban.getReason()).color(ChatColor.AQUA).create());
            }
            List<String> users = SqlUtil.getNamesFromIP(args[0]);
            if (users == null || users.isEmpty()) {
                if (!args[0].contains("*")) {
                    sender.sendMessage(ChatColor.RED + "No users found on that IP Address.");
                }
                return;
            }
            ComponentBuilder ulist = new ComponentBuilder("");
            for (int i = 0; i < users.size(); i++) {
                String s = users.get(i);
                if (i == (users.size() - 1)) {
                    ulist.append(s).color(ChatColor.GREEN).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bseen "
                            + s)).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            new ComponentBuilder("Click to search this Player!").color(ChatColor.GREEN).create()));
                    continue;
                }
                ulist.append(s).color(ChatColor.GREEN).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bseen "
                        + s)).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click to search this Player!").color(ChatColor.GREEN).create())).append(", ");
            }
            BaseComponent[] msg = new ComponentBuilder("Users on that IP Address (" + args[0] + "):").color(ChatColor.AQUA).create();
            sender.sendMessage(msg);
            sender.sendMessage(ulist.create());
            return;
        }
        sender.sendMessage(ChatColor.RED + "/ipseen [IP Address]");
    }
}