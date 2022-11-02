package us.mcmagic.magicbungee.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.Party;
import us.mcmagic.magicbungee.handlers.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class PartyUtil {
    public List<Party> partyList = new ArrayList<>();
    public HashMap<UUID, Party> timerList = new HashMap<>();

    public Party findPartyForPlayer(Player player) {
        return findPartyForPlayer(player.getUniqueId());
    }

    public Party findPartyForPlayer(CommandSender sender) {
        return findPartyForPlayer(((ProxiedPlayer) sender).getUniqueId());
    }

    public Party findPartyForPlayer(UUID uuid) {
        for (Party p : new ArrayList<>(partyList)) {
            if (p == null) {
                try {
                    partyList.remove(p);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (p.getLeader() == null) {
                p.close();
            }
            if (p.getMembers().contains(uuid) || p.getLeader().equals(uuid)) {
                return p;
            }
        }
        return null;
    }

    public void invitePlayer(final Party party, final Player tp) {
        if (timerList.containsKey(tp.getUniqueId())) {
            party.getLeader().sendMessage(new ComponentBuilder("This player already has a party request pending!")
                    .color(ChatColor.GREEN).create());
            return;
        }
        Party p = findPartyForPlayer(tp.getUniqueId());
        if (p != null) {
            if (p.getMembers().size() > 1 || hasTimer(p)) {
                party.getLeader().sendMessage(new ComponentBuilder("This player is already in a Party!")
                        .color(ChatColor.RED).create());
                return;

            }
            partyList.remove(p);
        }
        if (party.getMembers().contains(tp.getUniqueId())) {
            party.getLeader().sendMessage(new ComponentBuilder("This player is already in your party!")
                    .color(ChatColor.RED).create());
            return;
        }
        timerList.put(tp.getUniqueId(), party);
        tp.sendMessage(new ComponentBuilder(party.getLeader().getName()).color(ChatColor.YELLOW)
                .append(" has invited you to their Party! ").color(ChatColor.GREEN).append("Click here to join the Party.")
                .color(ChatColor.GOLD).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder("Click to join this Party!").color(ChatColor.AQUA).create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept"))
                .append(" This invite will expire in 5 minutes.", ComponentBuilder.FormatRetention.NONE)
                .color(ChatColor.GREEN).create());
        party.messageToAllMembers(new ComponentBuilder(party.getLeader().getName() + " has asked " + tp.getName() +
                " to join their party, they have 5 minutes to accept!").color(ChatColor.YELLOW).create(), true);
        MagicBungee.getProxyServer().getScheduler().schedule(MagicBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (timerList.containsKey(tp.getUniqueId()) && timerList.get(tp.getUniqueId()).getUniqueId()
                        .equals(party.getUniqueId())) {
                    timerList.remove(tp.getUniqueId());
                    tp.sendMessage(new ComponentBuilder("").create());
                    party.messageToAllMembers(new ComponentBuilder(party.getLeader().getName() + "'s request to " +
                            tp.getName() + " has expired!").color(ChatColor.YELLOW).create(), true);
                }
            }
        }, 5, TimeUnit.MINUTES);
    }

    private boolean hasTimer(Party p) {
        for (Map.Entry<UUID, Party> entry : timerList.entrySet()) {
            if (entry.getValue().getUniqueId().equals(p.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public void logout(Player player) {
        Party party = findPartyForPlayer(player);
        if (party == null) {
            return;
        }
        if (party.isLeader(player)) {
            party.close();
            partyList.remove(party);
            return;
        }
        party.leave(player);
    }

    public Party createParty(Player player) {
        Party party = new Party(player.getUniqueId(), new ArrayList<UUID>());
        partyList.add(party);
        return party;
    }

    public void removeParty(Party party) {
        partyList.remove(party);
    }

    public void acceptRequest(Player player) {
        if (!timerList.containsKey(player.getUniqueId())) {
            player.sendMessage(new ComponentBuilder("You have no pending Party Requests!").color(ChatColor.RED).create());
            return;
        }
        Party party = timerList.remove(player.getUniqueId());
        party.addMember(player);
        party.messageToAllMembers(new ComponentBuilder(player.getName() + " has accepted the Party Request!")
                .color(ChatColor.YELLOW).create(), true);
    }

    public void denyRequest(Player player) {
        if (!timerList.containsKey(player.getUniqueId())) {
            player.sendMessage(new ComponentBuilder("You have no pending Party Requests!").color(ChatColor.RED).create());
            return;
        }
        timerList.remove(player.getUniqueId());
        player.sendMessage(new ComponentBuilder("You have denied the Party Request!")
                .color(ChatColor.RED).create());
    }

    public List<Party> getParties() {
        return new ArrayList<>(partyList);
    }
}