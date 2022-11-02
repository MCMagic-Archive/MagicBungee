package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;

import java.io.File;
import java.io.IOException;

public class Commandmotdrl extends MagicCommand {

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        File motd = new File("plugins/MagicBungee/motd.yml");
        try {
            Configuration motdfile = ConfigurationProvider.getProvider(YamlConfiguration.class).load(motd);
            MagicBungee.motd = motdfile.getString("motd");
            MagicBungee.motdmaintenance = motdfile.getString("motd-maintenance");
            MagicBungee.info = motdfile.getStringList("infolist");
            sender.sendMessage(new ComponentBuilder("MOTD has been updated!").color(ChatColor.BLUE).create());
        } catch (IOException e) {
            e.printStackTrace();
            sender.sendMessage(new ComponentBuilder("There was an error!").color(ChatColor.RED).create());
        }
    }
}