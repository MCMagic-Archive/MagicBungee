package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.permissions.Rank;

public class Commanddisconnectall extends MagicCommand {

    public Commanddisconnectall() {
        super(Rank.CASTMEMBER);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 0) {
            for (ProxiedPlayer player : MagicBungee.getProxyServer().getPlayers()) {
                player.disconnect(ChatColor.AQUA
                        + "Please Pardon our Pixie Dust! We will be right back.");
            }
        } else {
            String kmsg = "";
            for (String arg : args) {
                kmsg += arg + " ";
            }
            for (ProxiedPlayer player : MagicBungee.getProxyServer().getPlayers()) {
                player.disconnect(ChatColor.AQUA + "" + ChatColor.translateAlternateColorCodes('&', kmsg));
            }
        }
    }
}
