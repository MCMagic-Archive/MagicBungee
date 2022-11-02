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
import us.mcmagic.magicbungee.utils.SqlUtil;

import java.util.Date;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class Commandban extends MagicCommand {

    public Commandban() {
        super(Rank.CASTMEMBER);
        tabCompletePlayers = true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }
        Player banner = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        if (args.length < 2) {
            banner.sendMessage(new TextComponent(ChatColor.RED + "/ban [Player] [Reason]"));
            return;
        }
        String playername = args[0];
        UUID uuid;
        try {
            uuid = UUID.fromString(SqlUtil.uuidFromUsername(playername));
        } catch (Exception ignored) {
            sender.sendMessage(new TextComponent(ChatColor.RED
                    + "I can't find that player!"));
            return;
        }
        String r = "";
        for (int i = 1; i < args.length; i++) {
            r += args[i] + " ";
        }
        String reason = r.substring(0, 1).toUpperCase() + r.substring(1);
        reason = reason.trim();
        if (BanUtil.isBannedPlayer(uuid)) {
            banner.sendMessage(ChatColor.RED + "This player is already banned! Unban them to change the reason");
            return;
        }
        BanUtil.banPlayer(uuid, reason, true, new Date(System.currentTimeMillis()), banner.getName());
        try {
            MagicBungee.getProxyServer().getPlayer(args[0]).disconnect(new TextComponent(ChatColor.RED +
                    "You Have Been Banned For " + ChatColor.AQUA + reason));
        } catch (Exception ignored) {
        }
        MagicBungee.dashboard.announceBan(new Ban(uuid, playername, true, System.currentTimeMillis(), reason, banner.getName()));
    }
}