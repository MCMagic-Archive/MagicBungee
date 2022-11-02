package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Party;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 7/3/15
 */
public class Commandparties extends MagicCommand {

    public Commandparties() {
        super(Rank.CASTMEMBER);
        tabCompletePlayers = true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length != 2 || !args[0].equalsIgnoreCase("info")) {
            List<Party> parties = MagicBungee.partyUtil.getParties();
            if (parties.isEmpty()) {
                sender.sendMessage(new ComponentBuilder("There are no Parties right now!").color(ChatColor.RED).create());
                return;
            }
            sender.sendMessage(new ComponentBuilder("Server Parties:").color(ChatColor.YELLOW).create());
            String msg = null;
            for (Party p : parties) {
                String leader = p.getLeader().getName();
                if (msg != null) {
                    msg += "\n";
                } else {
                    msg = "";
                }
                msg += "- " + leader + " " + p.getMembers().size() + " Member" + (p.getMembers().size() > 1 ? "s" : "");
            }
            sender.sendMessage(new ComponentBuilder(msg).color(ChatColor.GREEN).create());
            sender.sendMessage(new ComponentBuilder("/parties info [Party Leader] ").color(ChatColor.YELLOW)
                    .append("- Display info on that Party").color(ChatColor.GREEN).create());
            return;
        }
        Player tp = MagicBungee.getPlayer(args[1]);
        if (tp == null) {
            sender.sendMessage(new ComponentBuilder("Player not found!").color(ChatColor.RED).create());
            return;
        }
        Party p = MagicBungee.partyUtil.findPartyForPlayer(tp.getUniqueId());
        if (p == null) {
            sender.sendMessage(new ComponentBuilder("This player is not in a Party!").color(ChatColor.RED).create());
            return;
        }
        List<UUID> members = p.getMembers();
        List<String> names = new ArrayList<>();
        for (UUID uuid : members) {
            names.add(MagicBungee.getPlayer(uuid).getName());
        }
        String msg = "Party Leader: " + p.getLeader().getName() + "\nParty Members: ";
        for (int i = 0; i < names.size(); i++) {
            msg += names.get(i);
            if (i < (names.size() - 1)) {
                msg += ", ";
            }
        }
        sender.sendMessage(new ComponentBuilder(msg).color(ChatColor.YELLOW).create());
    }
}