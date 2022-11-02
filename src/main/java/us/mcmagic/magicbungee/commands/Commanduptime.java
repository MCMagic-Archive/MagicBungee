package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.utils.DateUtil;

public class Commanduptime extends MagicCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        TextComponent msg = new TextComponent();
        msg.setText("It has been ");
        msg.addExtra(DateUtil.formatDateDiff(MagicBungee.enableTime)
                + " since MCMagic's last reboot.");
        msg.setColor(ChatColor.GREEN);
        sender.sendMessage(msg);
    }
}