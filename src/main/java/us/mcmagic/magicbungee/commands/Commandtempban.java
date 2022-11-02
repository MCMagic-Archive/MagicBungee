package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.Ban;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.BanUtil;
import us.mcmagic.magicbungee.utils.DateUtil;
import us.mcmagic.magicbungee.utils.SqlUtil;

import java.util.Date;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class Commandtempban extends MagicCommand {

    public Commandtempban() {
        super(Rank.CASTMEMBER);
        tabCompletePlayers = true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        try {
            if (!(sender instanceof ProxiedPlayer)) {
                return;
            }
            Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
            if (args.length < 3) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "/tempban [Player] [Time] [Reason]"));
                sender.sendMessage(new TextComponent(ChatColor.RED + "Time Examples:"));
                sender.sendMessage(new TextComponent(ChatColor.RED + "6h = Six Hours"));
                sender.sendMessage(new TextComponent(ChatColor.RED + "6d = Six Days"));
                sender.sendMessage(new TextComponent(ChatColor.RED + "6w = Six Weeks"));
                sender.sendMessage(new TextComponent(ChatColor.RED + "6mon = Six Months"));
                return;
            }
            String playername = args[0].toLowerCase();
            if (!SqlUtil.exists(playername)) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "I can't find that player!"));
                return;
            }
            String r = "";
            for (int i = 2; i < args.length; i++) {
                r += args[i] + " ";
            }
            String reason = r.substring(0, 1).toUpperCase() + r.substring(1);
            reason = reason.trim();
            String time = args[1];
            long banTimestamp = DateUtil.parseDateDiff(time, true);
            Player tpuser = MagicBungee.getPlayer(playername);
            UUID uuid;
            if (tpuser == null) {
                uuid = UUID.fromString(SqlUtil.uuidFromUsername(playername));
            } else {
                uuid = tpuser.getUniqueId();
            }
            if (BanUtil.getBan(uuid, playername) != null) {
                player.sendMessage(ChatColor.RED + "This player is already banned! Unban them to change the reason/length");
                return;
            }
            BanUtil.banPlayer(uuid, reason, false, new Date(banTimestamp), player.getName());
            try {
                MagicBungee.getPlayer(uuid).kickPlayer(new TextComponent(ChatColor.RED +
                        "You Have Been Temporarily Banned For " + ChatColor.AQUA + reason + ". " + ChatColor.RED +
                        "Your Temporary Ban Will Expire in " + ChatColor.AQUA + DateUtil.formatDateDiff(banTimestamp)));
            } catch (Exception ignored) {
            }
            MagicBungee.dashboard.announceBan(new Ban(uuid, playername, false, banTimestamp, reason, player.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}