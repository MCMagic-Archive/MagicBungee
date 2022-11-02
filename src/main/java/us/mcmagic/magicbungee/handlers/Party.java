package us.mcmagic.magicbungee.handlers;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.ChatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {
    private UUID leader;
    private List<UUID> members;
    public BaseComponent[] headerMessage = new ComponentBuilder("-----------------------------------------------------")
            .color(ChatColor.GOLD).create();
    public BaseComponent[] footerMessage = new ComponentBuilder("-----------------------------------------------------")
            .color(ChatColor.GOLD).create();
    public BaseComponent[] warpMessage = new ComponentBuilder("Your Party Leader has warped you to their server.")
            .color(ChatColor.GREEN).create();
    private UUID uuid = UUID.randomUUID();

    public Party(UUID leader, List<UUID> members) {
        this.leader = leader;
        this.members = members;
        if (!members.contains(leader)) {
            members.add(leader);
        }
    }

    public Player getLeader() {
        if (leader == null) {
            return null;
        }
        return MagicBungee.getPlayer(leader);
    }

    public List<UUID> getMembers() {
        return new ArrayList<>(members);
    }

    public void addMember(Player player) {
        if (members.contains(player.getUniqueId())) {
            return;
        }
        members.add(player.getUniqueId());
    }

    public void removeMember(Player player) {
        if (!members.contains(player.getUniqueId())) {
            return;
        }
        members.remove(player.getUniqueId());
    }

    public void close() {
        String name = null;
        Player lead = MagicBungee.getPlayer(leader);
        if (lead != null) {
            name = lead.getName();
        }
        messageToAllMembers(new ComponentBuilder(name == null ? "The Party has been closed!" : name +
                " has closed the Party!").color(ChatColor.RED).create(), true);
        members.clear();
        MagicBungee.partyUtil.removeParty(this);
    }

    public void warpToLeader() {
        if (members.size() > 25) {
            MagicBungee.getPlayer(leader).sendMessage(new ComponentBuilder("Parties larger than 25 players cannot be warped!")
                    .color(ChatColor.RED).create());
            return;
        }
        ServerInfo server = MagicBungee.getPlayer(leader).getServer().getInfo();
        for (UUID tuuid : members) {
            Player tp = MagicBungee.getPlayer(tuuid);
            if (tp == null) {
                continue;
            }
            if (tp.getUniqueId().equals(leader)) {
                continue;
            }
            MagicBungee.getProxyServer().getPlayer(tp.getUniqueId()).connect(server);
        }
        messageToAllMembers(warpMessage, true);
    }

    public boolean isLeader(Player player) {
        return leader.equals(player.getUniqueId());
    }

    public BaseComponent[] getHeaderMessage() {
        return headerMessage;
    }

    public BaseComponent[] getFooterMessage() {
        return footerMessage;
    }

    public void messageToAllMembers(BaseComponent[] message, boolean bars) {
        for (UUID tuuid : getMembers()) {
            Player tp = MagicBungee.getPlayer(tuuid);
            if (tp == null) {
                continue;
            }
            if (bars) {
                tp.sendMessage(headerMessage);
            }
            tp.sendMessage(message);
            if (bars) {
                tp.sendMessage(footerMessage);
            }
        }
    }

    public void listMembersToMember(Player player) {
        TextComponent msg = new TextComponent();
        msg.setText("Members of your Party: ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < members.size(); i++) {
            boolean l = members.get(i).equals(leader);
            if (i == (members.size() - 1)) {
                sb.append(l ? "*" : "").append(MagicBungee.getPlayer(members.get(i)).getName());
                continue;
            }
            sb.append(l ? "*" : "").append(MagicBungee.getPlayer(members.get(i)).getName()).append(", ");
        }
        msg.addExtra(sb.toString());
        msg.setColor(ChatColor.YELLOW);
        player.sendMessage(headerMessage);
        player.sendMessage(msg);
        player.sendMessage(footerMessage);
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public void leave(Player player) {
        removeMember(player);
        messageToAllMembers(new ComponentBuilder(player.getName()).color(ChatColor.RED)
                .append(" has left the Party!").color(ChatColor.YELLOW).create(), true);
        player.sendMessage(new ComponentBuilder("You have left the party!").color(ChatColor.RED).create());
    }

    public void remove(Player tp) {
        if (!getMembers().contains(tp.getUniqueId())) {
            MagicBungee.getPlayer(leader).sendMessage(new ComponentBuilder("That user is not in your Party!")
                    .color(ChatColor.YELLOW).create());
            return;
        }
        removeMember(tp);
        messageToAllMembers(new ComponentBuilder(MagicBungee.getPlayer(leader).getName() + " has removed " +
                tp.getName() + " from the Party!").color(ChatColor.YELLOW).create(), true);
    }

    public void chat(Player player, String msg) {
        Rank r = player.getRank();
        BaseComponent[] m = new ComponentBuilder("[Party] ").color(ChatColor.BLUE)
                .append(leader.equals(player.getUniqueId()) ? "* " : "").color(ChatColor.YELLOW)
                .append(r.getNameWithBrackets() + " ").append(player.getName() + ": ").color(ChatColor.GRAY).append(msg)
                .color(ChatColor.WHITE).create();
        for (UUID uuid : getMembers()) {
            if (uuid.equals(player.getUniqueId())) {
                continue;
            }
            Player tp = MagicBungee.getPlayer(uuid);
            if (tp != null && tp.hasMentions()) {
                ChatUtil.mentionSound(tp);
            }
        }
        messageToAllMembers(m, false);
        ChatUtil.socialSpyParty(player, this, msg, "pchat");
    }

    public void promote(Player player, Player tp) {
        messageToAllMembers(new ComponentBuilder(player.getName() + " promoted " + tp.getName() + " to Party Leader!")
                .color(ChatColor.YELLOW).create(), true);
        leader = tp.getUniqueId();
    }

    public void takeover(Player player) {
        if (!members.contains(player.getUniqueId())) {
            members.add(player.getUniqueId());
        }
        leader = player.getUniqueId();
        messageToAllMembers(new ComponentBuilder(player.getName() + " has taken over the Party!")
                .color(ChatColor.YELLOW).create(), true);
    }
}