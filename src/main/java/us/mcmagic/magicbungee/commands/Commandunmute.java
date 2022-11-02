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
import us.mcmagic.magicbungee.utils.MuteUtil;
import us.mcmagic.magicbungee.utils.SqlUtil;

import java.util.UUID;

public class Commandunmute extends MagicCommand {

    public Commandunmute() {
        super(Rank.EARNINGMYEARS);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            if (args.length < 1) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "/unmute [Player]"));
                return;
            }
            Player tp = MagicBungee.getPlayer(args[0]);
            if (tp == null) {
                try {
                    UUID uuid = UUID.fromString(SqlUtil.uuidFromUsername(args[0]));
                    if (!MuteUtil.isMuted(uuid)) {
                        sender.sendMessage(new ComponentBuilder(args[0] + " is not muted!").color(ChatColor.RED).create());
                        return;
                    }
                    MagicBungee.dashboard.announceUnmute(new Mute(uuid, args[0], true, System.currentTimeMillis(), "",
                            ""), "Console");
                    MuteUtil.unmutePlayer(uuid);
                    return;
                } catch (Exception ignored) {
                    sender.sendMessage(new TextComponent(ChatColor.RED + "I can't find that player!"));
                    return;
                }
            }
            UUID uuid = tp.getUniqueId();
            String playername = tp.getName();
            if (!tp.getMute().isMuted()) {
                sender.sendMessage(new TextComponent(ChatColor.RED + playername + " is not muted!"));
                return;
            }
            MuteUtil.unmutePlayer(uuid);
            tp.getMute().setMuted(false);
            tp.getMute().setRelease(System.currentTimeMillis());
            TextComponent msg = new TextComponent();
            msg.setText("You have been unsilenced.");
            tp.sendMessage(msg);
            MagicBungee.dashboard.announceUnmute(tp.getMute(), "Console");
            return;
        }
        Player unmuter = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        if (args.length < 1) {
            unmuter.sendMessage(new TextComponent(ChatColor.RED + "/unmute [Player]"));
            return;
        }
        Player tp = MagicBungee.getPlayer(args[0]);
        if (tp == null) {
            try {
                UUID uuid = UUID.fromString(SqlUtil.uuidFromUsername(args[0]));
                if (!MuteUtil.isMuted(uuid)) {
                    sender.sendMessage(new ComponentBuilder(args[0] + " is not muted!").color(ChatColor.RED).create());
                    return;
                }
                MagicBungee.dashboard.announceUnmute(new Mute(uuid, args[0], true, System.currentTimeMillis(), "", ""),
                        unmuter.getName());
                MuteUtil.unmutePlayer(uuid);
                return;
            } catch (Exception ignored) {
                sender.sendMessage(new TextComponent(ChatColor.RED + "I can't find that player!"));
                return;
            }
        }
        UUID uuid = tp.getUniqueId();
        String playername = tp.getName();
        if (!tp.getMute().isMuted()) {
            unmuter.sendMessage(new TextComponent(ChatColor.RED + playername + " is not muted!"));
            return;
        }
        MuteUtil.unmutePlayer(uuid);
        tp.getMute().setMuted(false);
        tp.getMute().setRelease(System.currentTimeMillis());
        TextComponent msg = new TextComponent();
        msg.setText("You have been unsilenced.");
        msg.setColor(ChatColor.RED);
        tp.sendMessage(msg);
        MagicBungee.dashboard.announceUnmute(tp.getMute(), unmuter.getName());
    }
}