package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;

import java.io.File;
import java.io.IOException;

public class Commandmaintenance extends MagicCommand {

    public Commandmaintenance() {
        super(Rank.DEVELOPER);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (MagicBungee.maintenance) {
            MagicBungee.maintenance = false;
            sender.sendMessage(ChatColor.RED + "Maintenance Mode has been disabled!");
        } else {
            MagicBungee.maintenance = true;
            sender.sendMessage(ChatColor.RED + "Maintenance Mode has been enabled!");
            for (Player tp : MagicBungee.getOnlinePlayers()) {
                if (tp.getRank().getRankId() < Rank.CASTMEMBER.getRankId()) {
                    tp.kickPlayer(ChatColor.RED + "Maintenance Mode has been enabled. Be back soon!");
                }
            }
        }
        try {
            File file = new File("plugins/MagicBungee/motd.yml");
            Configuration motdfile = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            motdfile.set("maintenance-mode", MagicBungee.maintenance);
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(motdfile, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}