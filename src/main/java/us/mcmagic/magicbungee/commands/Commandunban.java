package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.BanUtil;
import us.mcmagic.magicbungee.utils.SqlUtil;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class Commandunban extends MagicCommand {

    public Commandunban() {
        super(Rank.CASTMEMBER);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            if (args.length < 1) {
                sender.sendMessage(new TextComponent(ChatColor.RED
                        + "/unban [Player]"));
                return;
            }
            String playername = args[0].toLowerCase();
            UUID uuid;
            try {
                uuid = UUID.fromString(SqlUtil.uuidFromUsername(playername));
            } catch (Exception ignored) {
                sender.sendMessage(new TextComponent(ChatColor.RED
                        + "I can't find that player!"));
                return;
            }
            if (!BanUtil.isBannedPlayer(uuid)) {
                sender.sendMessage(new TextComponent(ChatColor.RED + playername
                        + " is not banned!"));
                return;
            }
            BanUtil.unbanPlayer(uuid);
            MagicBungee.dashboard.announceUnban(playername, "Console");
            return;
        }
        Player unbanner = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        if (args.length < 1) {
            unbanner.sendMessage(new TextComponent(ChatColor.RED
                    + "/unban [Player]"));
            return;
        }
        String playername = args[0];
        UUID uuid;
        try {
            uuid = UUID.fromString(SqlUtil.uuidFromUsername(playername));
        } catch (Exception ignored) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "I can't find that player!"));
            return;
        }
        if (!BanUtil.isBannedPlayer(uuid)) {
            unbanner.sendMessage(new TextComponent(ChatColor.RED + playername + " is not banned!"));
            return;
        }
        BanUtil.unbanPlayer(uuid);
        MagicBungee.dashboard.announceUnban(playername, unbanner.getName());
    }
}