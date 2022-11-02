package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.Kick;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.SqlUtil;

@SuppressWarnings("deprecation")
public class Commandkick extends MagicCommand {

    public Commandkick() {
        super(Rank.EARNINGMYEARS);
        tabCompletePlayers = true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "/kick [Player] [Reason]");
                return;
            }
            Player tp = MagicBungee.getPlayer(args[0]);
            if (tp == null) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "I can't find that player!"));
                return;
            }
            String r = "";
            for (int i = 1; i < args.length; i++) {
                r += args[i] + " ";
            }
            String reason = r.substring(0, 1).toUpperCase() + r.substring(1);
            reason = reason.trim();
            tp.kickPlayer(reason);
            MagicBungee.dashboard.announceKick(tp.getName(), reason, "Console");
            SqlUtil.logKick(new Kick(tp.getUniqueId(), reason, "Console"));
            return;
        }
        Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/kick [Player] [Reason]");
            return;
        }
        Player tp = MagicBungee.getPlayer(args[0]);
        if (tp == null) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "I can't find that player!"));
            return;
        }
        if (tp.isKicking()) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "I can't find that player!"));
            return;
        }
        String r = "";
        for (int i = 1; i < args.length; i++) {
            r += args[i] + " ";
        }
        String reason = r.substring(0, 1).toUpperCase() + r.substring(1);
        reason = reason.trim();
        tp.kickPlayer(reason);
        try {
            MagicBungee.getProxyServer().getPlayer(args[0]).disconnect(ChatColor.RED + "You have been disconnected for: "
                    + ChatColor.AQUA + reason);
            MagicBungee.dashboard.announceKick(tp.getName(), reason, player.getName());
            SqlUtil.logKick(new Kick(tp.getUniqueId(), reason, player.getName()));
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "That player isn't online!");
        }
    }
}