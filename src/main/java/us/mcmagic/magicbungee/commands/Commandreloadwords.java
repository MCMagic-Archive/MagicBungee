package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.permissions.Rank;

import java.io.File;
import java.io.IOException;

public class Commandreloadwords extends MagicCommand {

    public Commandreloadwords() {
        super(Rank.CASTMEMBER);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(ChatColor.BLUE + "Reloading Words...");
        MagicBungee.swearList.clear();
        MagicBungee.swearListSpecific.clear();
        Configuration config;
        try {
            File file = new File("plugins/MagicBungee/config.yml");
            config = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(file);
            for (String word : config.getStringList("swear-list.list")) {
                MagicBungee.swearList.add(word);
            }
            for (String word : config.getStringList("swear-list.specific")) {
                MagicBungee.swearListSpecific.add(word);
            }
            sender.sendMessage(ChatColor.BLUE + "Words Reloaded!");
        } catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "There was an error!");
            e.printStackTrace();
        }
    }
}