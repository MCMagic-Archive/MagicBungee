package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Mute;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.DateUtil;
import us.mcmagic.magicbungee.utils.MuteUtil;
import us.mcmagic.magicbungee.utils.SqlUtil;

import java.util.Date;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class Commandmute extends MagicCommand {

    public Commandmute() {
        super(Rank.EARNINGMYEARS);
        tabCompletePlayers = true;
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        try {
            if (!(sender instanceof ProxiedPlayer)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command!");
                return;
            }
            Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "/mute [Player] [Time] [Reason]");
                sender.sendMessage(new TextComponent(ChatColor.RED + "Time Examples:"));
                sender.sendMessage(new TextComponent(ChatColor.RED + "5m = Five Minutes"));
                sender.sendMessage(new TextComponent(ChatColor.RED + "1h = One Hour"));
                return;
            }
            String playername = args[0];
            long muteTimestamp = DateUtil.parseDateDiff(args[1], true);
            long length = muteTimestamp - System.currentTimeMillis();
            if (length > 3600000) {
                player.sendMessage(new ComponentBuilder("The maximum mute length is 1 hour!").color(ChatColor.RED)
                        .create());
                return;
            }
            String r = "";
            if (args.length > 2) {
                for (int i = 2; i < args.length; i++) {
                    r += args[i] + " ";
                }
            }
            String reason = r.substring(0, 1).toUpperCase() + r.substring(1);
            reason = reason.trim();
            Player tp = MagicBungee.getPlayer(args[0]);
            if (tp == null) {
                try {
                    UUID uuid = UUID.fromString(SqlUtil.uuidFromUsername(args[0]));
                    if (MuteUtil.isMuted(uuid)) {
                        player.sendMessage(ChatColor.RED + "This player is already muted! Unmute them to change the reason/length");
                        return;
                    }
                    MagicBungee.dashboard.announceMute(new Mute(uuid, playername, true, muteTimestamp, reason,
                            player.getName()));
                    MuteUtil.mutePlayer(uuid, new Date(muteTimestamp), reason, player.getName());
                    return;
                } catch (Exception ignored) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "I can't find that player!"));
                    return;
                }
            }
            UUID uuid = tp.getUniqueId();
            if (tp.getMute().isMuted()) {
                player.sendMessage(ChatColor.RED + "This player is already muted! Unmute them to change the reason/length");
                return;
            }
            MuteUtil.mutePlayer(uuid, new Date(muteTimestamp), reason, player.getName());
            TextComponent msg = new TextComponent();
            msg.setText("You Have Been Silenced For " + DateUtil.formatDateDiff(muteTimestamp) + ".");
            if (!reason.equals("")) {
                msg.addExtra(" Reason: " + reason);
            }
            msg.setColor(ChatColor.RED);
            tp.getMute().setMuted(true);
            tp.getMute().setRelease(muteTimestamp);
            tp.getMute().setSource(player.getName());
            tp.sendMessage(msg);
            if (!reason.equals("")) {
                tp.getMute().setReason(reason);
            }
            MagicBungee.dashboard.announceMute(tp.getMute());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}