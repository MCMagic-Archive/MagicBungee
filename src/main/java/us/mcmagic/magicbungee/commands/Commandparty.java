package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Party;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;

import java.util.Collections;

@SuppressWarnings("deprecation")
public class Commandparty extends MagicCommand {

    public Commandparty() {
        super(Rank.GUEST);
        aliases = Collections.singletonList("p");
        tabCompletePlayers = true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(new ComponentBuilder("Only players can use this!").color(ChatColor.RED).create());
            return;
        }
        Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                helpMenu(sender);
                return;
            }
            if (args[0].equalsIgnoreCase("accept")) {
                MagicBungee.partyUtil.acceptRequest(player);
                return;
            }
            if (args[0].equalsIgnoreCase("deny")) {
                MagicBungee.partyUtil.denyRequest(player);
                return;
            }
            Party party = MagicBungee.partyUtil.findPartyForPlayer(player);
            if (args[0].equalsIgnoreCase("close")) {
                if (party == null) {
                    player.sendMessage(new ComponentBuilder("You're not in a Party!").color(ChatColor.RED).create());
                    return;
                }
                if (!party.isLeader(player)) {
                    player.sendMessage(new ComponentBuilder("Only the Party Leader can use this!").color(ChatColor.RED)
                            .create());
                    return;
                }
                party.close();
                return;
            }
            if (args[0].equalsIgnoreCase("leave")) {
                if (party == null) {
                    player.sendMessage(new ComponentBuilder("You're not in a Party!").color(ChatColor.RED).create());
                    return;
                }
                if (party.isLeader(player)) {
                    player.sendMessage(new ComponentBuilder("You cannot leave the Party, you're the Leader!")
                            .color(ChatColor.RED).create());
                    return;
                }
                party.leave(player);
                return;
            }
            if (args[0].equalsIgnoreCase("list")) {
                if (party == null) {
                    player.sendMessage(new ComponentBuilder("You're not in a Party!").color(ChatColor.RED).create());
                    return;
                }
                party.listMembersToMember(player);
                return;
            }
            if (args[0].equalsIgnoreCase("warp")) {
                if (party == null) {
                    player.sendMessage(new ComponentBuilder("You're not in a Party!").color(ChatColor.RED).create());
                    return;
                }
                if (!party.isLeader(player)) {
                    player.sendMessage(new ComponentBuilder("Only the Party Leader can use this!").color(ChatColor.RED)
                            .create());
                    return;
                }
                party.warpToLeader();
                return;
            }
            if (label.equalsIgnoreCase("p") && args[0].length() < 3 && player.getServer().getInfo().getName()
                    .contains("Creative")) {
                player.sendMessage(new ComponentBuilder("Sorry, /p is used for our Party System now!")
                        .color(ChatColor.RED).create());
                return;
            }
            if (party == null) {
                party = MagicBungee.partyUtil.createParty(player);
            }
            if (!party.isLeader(player)) {
                player.sendMessage(new ComponentBuilder("Only the Party Leader can invite players!").color(ChatColor.RED)
                        .create());
                return;
            }
            Player tp = MagicBungee.getPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(new ComponentBuilder("That player wasn't found!").color(ChatColor.RED).create());
                return;
            }
            if (tp.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage(new ComponentBuilder("You cannot invite yourself!").color(ChatColor.RED).create());
                return;
            }
            MagicBungee.partyUtil.invitePlayer(party, tp);
            return;
        }
        if (args.length == 2) {
            Party party = MagicBungee.partyUtil.findPartyForPlayer(player);
            if (args[0].equalsIgnoreCase("takeover")) {
                if (player.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                    helpMenu(sender);
                    return;
                }
                Player tp = MagicBungee.getPlayer(args[1]);
                if (tp == null) {
                    player.sendMessage(new ComponentBuilder("That player wasn't found!").color(ChatColor.RED).create());
                    return;
                }
                if (party != null) {
                    if (!party.getMembers().contains(tp.getUniqueId())) {
                        player.sendMessage(new ComponentBuilder("You must first leave your current Party!")
                                .color(ChatColor.RED).create());
                        return;
                    }
                }
                party = MagicBungee.partyUtil.findPartyForPlayer(tp.getUniqueId());
                if (party == null) {
                    player.sendMessage(new ComponentBuilder("This player is not in a Party!").color(ChatColor.RED)
                            .create());
                    return;
                }
                if (party.isLeader(player)) {
                    player.sendMessage(new ComponentBuilder("You are already the Party Leader!").color(ChatColor.RED)
                            .create());
                    return;
                }
                party.takeover(player);
                return;
            }
            if (party == null) {
                player.sendMessage(new ComponentBuilder("You're not in a Party!").color(ChatColor.RED).create());
                return;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (!party.isLeader(player)) {
                    player.sendMessage(new ComponentBuilder("Only the Party Leader can use this!").color(ChatColor.RED)
                            .create());
                    return;
                }
                Player tp = MagicBungee.getPlayer(args[1]);
                if (tp == null) {
                    player.sendMessage(new ComponentBuilder("That player wasn't found!").color(ChatColor.RED).create());
                    return;
                }
                party.remove(tp);
                return;
            }
            if (args[0].equalsIgnoreCase("promote")) {
                if (!party.isLeader(player)) {
                    player.sendMessage(new ComponentBuilder("Only the Party Leader can use this!").color(ChatColor.RED)
                            .create());
                    return;
                }
                Player tp = MagicBungee.getPlayer(args[1]);
                if (tp == null) {
                    player.sendMessage(new ComponentBuilder("That player wasn't found!").color(ChatColor.RED).create());
                    return;
                }
                if (!party.getMembers().contains(tp.getUniqueId())) {
                    player.sendMessage(new ComponentBuilder("That player isn't in your Party!").color(ChatColor.RED).create());
                    return;
                }
                if (tp.getUniqueId().equals(player.getUniqueId())) {
                    player.sendMessage(new ComponentBuilder("You're already the Leader!").color(ChatColor.RED).create());
                }
                party.promote(player, tp);
                return;
            }
        }
        helpMenu(sender);
    }

    public void helpMenu(CommandSender sender) {
        sender.sendMessage(new TextComponent(ChatColor.YELLOW
                + "Party Help Menu: " + ChatColor.GRAY + "(Click for Example)"));
        HoverEvent clickForEx = new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN +
                "Click for an example!").create());
        TextComponent help = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/party help " +
                ChatColor.YELLOW + "- Shows this help menu");
        TextComponent list = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/party list " +
                ChatColor.YELLOW + "- Lists all of the members in your Party");
        TextComponent warp = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/party warp " +
                ChatColor.YELLOW + "- Brings the members of your Party to your server");
        TextComponent remove = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/party remove [player] " +
                ChatColor.YELLOW + "- Removes a player from your Party");
        TextComponent accept = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/party accept " +
                ChatColor.YELLOW + "- Accepts a Party invite from a player");
        TextComponent close = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/party close " +
                ChatColor.YELLOW + "- Closes the Party");
        TextComponent leave = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/party leave " +
                ChatColor.YELLOW + "- Leaves your current Party");
        TextComponent chat = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/pchat [Message] " +
                ChatColor.YELLOW + "Message members of your Party");
        TextComponent howToStart = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/party [player] " +
                ChatColor.YELLOW + "- Invites a player to your Party");
        TextComponent deny = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/party deny " +
                ChatColor.YELLOW + "- Deny a Party Request");
        TextComponent pro = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA + "/party promote [player] " +
                ChatColor.YELLOW + "- Promote a player to Party Leader");
        help.setHoverEvent(clickForEx);
        howToStart.setHoverEvent(clickForEx);
        list.setHoverEvent(clickForEx);
        warp.setHoverEvent(clickForEx);
        remove.setHoverEvent(clickForEx);
        accept.setHoverEvent(clickForEx);
        close.setHoverEvent(clickForEx);
        leave.setHoverEvent(clickForEx);
        chat.setHoverEvent(clickForEx);
        deny.setHoverEvent(clickForEx);
        pro.setHoverEvent(clickForEx);
        help.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party help"));
        howToStart.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/party "));
        list.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party list"));
        warp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party warp"));
        remove.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/party remove "));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept"));
        close.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party close"));
        leave.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party leave"));
        chat.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/pchat "));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny "));
        pro.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/party promote "));
        sender.sendMessage(help);
        sender.sendMessage(howToStart);
        sender.sendMessage(leave);
        sender.sendMessage(list);
        sender.sendMessage(pro);
        sender.sendMessage(accept);
        sender.sendMessage(deny);
        sender.sendMessage(warp);
        sender.sendMessage(remove);
        sender.sendMessage(chat);
        sender.sendMessage(close);
    }

}