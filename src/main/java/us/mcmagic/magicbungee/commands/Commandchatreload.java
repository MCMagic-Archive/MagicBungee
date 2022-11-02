package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.listeners.ServerConnect;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.ChatUtil;

/**
 * Created by Marc on 3/21/15
 */
public class Commandchatreload extends MagicCommand {

    public Commandchatreload() {
        super(Rank.CASTMEMBER);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(new ComponentBuilder("Reloading servers...").color(ChatColor.BLUE).create());
        ChatUtil.reloadServers();
        ServerConnect.initialize();
        MagicBungee.statUtil.reload();
        sender.sendMessage(new ComponentBuilder("Servers reloaded!").color(ChatColor.BLUE).create());
    }
}
