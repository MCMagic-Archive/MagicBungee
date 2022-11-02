package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.listeners.PlayerJoinAndLeave;
import us.mcmagic.magicbungee.permissions.Rank;

/**
 * Created by Marc on 8/27/15
 */
public class Commandreboot extends MagicCommand {

    public Commandreboot() {
        super(Rank.DEVELOPER);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }
        Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        final String reason;
        if (args.length == 0) {
            reason = "Please Pardon our Pixie Dust! We are restarting our servers right now.";
        } else {
            String kmsg = "";
            for (String arg : args) {
                kmsg += arg + " ";
            }
            reason = kmsg;
        }
        PlayerJoinAndLeave.rebooting = true;
        MagicBungee.getProxyServer().getScheduler().runAsync(MagicBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (ProxiedPlayer player : MagicBungee.getProxyServer().getPlayers()) {
                    player.disconnect(ChatColor.AQUA + "" + ChatColor.translateAlternateColorCodes('&', reason));
                }
            }
        });
    }
}