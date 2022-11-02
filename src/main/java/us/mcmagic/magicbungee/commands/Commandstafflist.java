package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;

import java.util.ArrayList;
import java.util.List;

public class Commandstafflist extends MagicCommand {

    public Commandstafflist() {
        super(Rank.EARNINGMYEARS);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        List<Player> owners = new ArrayList<>();
        List<Player> mayors = new ArrayList<>();
        List<Player> managers = new ArrayList<>();
        List<Player> developers = new ArrayList<>();
        List<Player> coordinators = new ArrayList<>();
        List<Player> castmembers = new ArrayList<>();
        List<Player> earningmyears = new ArrayList<>();
        for (Player tp : MagicBungee.getOnlinePlayers()) {
            Rank r = tp.getRank();
            if (r.getRankId() >= Rank.EARNINGMYEARS.getRankId()) {
                switch (r) {
                    case EARNINGMYEARS:
                        earningmyears.add(tp);
                        break;
                    case CASTMEMBER:
                        castmembers.add(tp);
                        break;
                    case COORDINATOR:
                        coordinators.add(tp);
                        break;
                    case DEVELOPER:
                        developers.add(tp);
                        break;
                    case MANAGER:
                        managers.add(tp);
                        break;
                    case MAYOR:
                        mayors.add(tp);
                        break;
                    case OWNER:
                        owners.add(tp);
                        break;
                }
            }
        }
        ComponentBuilder o = new ComponentBuilder("Owners: (" + owners.size() + ") ").color(ChatColor.GOLD);
        ComponentBuilder ma = new ComponentBuilder("Mayors: (" + mayors.size() + ") ").color(ChatColor.GOLD);
        ComponentBuilder m = new ComponentBuilder("Managers: (" + managers.size() + ") ").color(ChatColor.GOLD);
        ComponentBuilder d = new ComponentBuilder("Developers: (" + developers.size() + ") ").color(ChatColor.GOLD);
        ComponentBuilder co = new ComponentBuilder("Coordinators: (" + coordinators.size() + ") ").color(ChatColor.GREEN);
        ComponentBuilder c = new ComponentBuilder("Cast Members: (" + castmembers.size() + ") ").color(ChatColor.GREEN);
        ComponentBuilder eme = new ComponentBuilder("Earning My Ears: (" + earningmyears.size() + ") ").color(ChatColor.GREEN);
        for (int i = 0; i < owners.size(); i++) {
            Player tp = owners.get(i);
            o.append(tp.getName(), ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Currently on: ")
                            .color(ChatColor.GREEN).append(tp.getServerName()).color(ChatColor.AQUA).create()));
            if (i < (owners.size() - 1)) {
                o.append(", ");
            }
        }
        for (int i = 0; i < mayors.size(); i++) {
            Player tp = mayors.get(i);
            ma.append(tp.getName(), ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Currently on: ")
                            .color(ChatColor.GREEN).append(tp.getServerName()).color(ChatColor.AQUA).create()));
            if (i < (mayors.size() - 1)) {
                ma.append(", ");
            }
        }
        for (int i = 0; i < managers.size(); i++) {
            Player tp = managers.get(i);
            m.append(tp.getName(), ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Currently on: ")
                            .color(ChatColor.GREEN).append(tp.getServerName()).color(ChatColor.AQUA).create()));
            if (i < (managers.size() - 1)) {
                m.append(", ");
            }
        }
        for (int i = 0; i < developers.size(); i++) {
            Player tp = developers.get(i);
            d.append(tp.getName(), ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Currently on: ")
                            .color(ChatColor.GREEN).append(tp.getServerName()).color(ChatColor.AQUA).create()));
            if (i < (developers.size() - 1)) {
                d.append(", ");
            }
        }
        for (int i = 0; i < castmembers.size(); i++) {
            Player tp = castmembers.get(i);
            c.append(tp.getName(), ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Currently on: ")
                            .color(ChatColor.GREEN).append(tp.getServerName()).color(ChatColor.AQUA).create()));
            if (i < (castmembers.size() - 1)) {
                c.append(", ");
            }
        }
        for (int i = 0; i < coordinators.size(); i++) {
            Player tp = coordinators.get(i);
            co.append(tp.getName(), ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Currently on: ")
                            .color(ChatColor.GREEN).append(tp.getServerName()).color(ChatColor.AQUA).create()));
            if (i < (coordinators.size() - 1)) {
                co.append(", ");
            }
        }
        for (int i = 0; i < earningmyears.size(); i++) {
            Player tp = earningmyears.get(i);
            eme.append(tp.getName(), ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Currently on: ")
                            .color(ChatColor.GREEN).append(tp.getServerName()).color(ChatColor.AQUA).create()));
            if (i < (earningmyears.size() - 1)) {
                eme.append(", ");
            }
        }
        sender.sendMessage(new ComponentBuilder("Online Staff Members:").color(ChatColor.GREEN).create());
        if (owners.size() > 0) {
            sender.sendMessage(o.create());
        }
        if (mayors.size() > 0) {
            sender.sendMessage(ma.create());
        }
        if (managers.size() > 0) {
            sender.sendMessage(m.create());
        }
        if (developers.size() > 0) {
            sender.sendMessage(d.create());
        }
        if (coordinators.size() > 0) {
            sender.sendMessage(co.create());
        }
        if (castmembers.size() > 0) {
            sender.sendMessage(c.create());
        }
        if (earningmyears.size() > 0) {
            sender.sendMessage(eme.create());
        }
    }
}