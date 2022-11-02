package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.permissions.Rank;

/**
 * Created by Marc on 7/2/15
 */
public class Commandend extends MagicCommand {

    public Commandend() {
        super(Rank.DEVELOPER);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            kickAll(ChatColor.AQUA + "We're restarting our servers, check back in a moment!");
            MagicBungee.getProxyServer().stop();
            return;
        }
        String reason = "";
        for (int i = 0; i < args.length; i++) {
            reason += ChatColor.translateAlternateColorCodes('&', args[i]);
            if (i < (args.length - 1)) {
                reason += " ";
            }
        }
        kickAll(reason);
        MagicBungee.getProxyServer().stop();
    }

    @SuppressWarnings("deprecation")
    private void kickAll(String reason) {
        for (ProxiedPlayer tp : MagicBungee.getProxyServer().getPlayers()) {
            tp.disconnect(reason);
        }
    }
}