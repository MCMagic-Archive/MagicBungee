package us.mcmagic.magicbungee.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 4/23/15
 */
public class ServerConnect implements Listener {
    private static List<String> invServers = new ArrayList<>();

    public ServerConnect() {
        initialize();
    }

    public static void initialize() {
        invServers.clear();
        File file = new File("plugins/MagicBungee/config.yml");
        Configuration chat = null;
        try {
            chat = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String s : chat.getStringList("inventory-servers")) {
            invServers.add(s);
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        Player player = MagicBungee.getPlayer(event.getPlayer().getUniqueId());
        if (player == null) {
            return;
        }
        ServerInfo target = event.getTarget();
        if ((player.getServer() == null || !invServers.contains(player.getServer().getInfo().getName())) &&
                invServers.contains(target.getName())) {
            try {
                sendUpdateRequest(target, player);
            } catch (IOException e) {
                player.sendMessage(new ComponentBuilder("Error with Inventory upload/download! " +
                        "(Error Code 108)").color(ChatColor.RED).create());
                e.printStackTrace();
            }
        }
    }

    private void sendUpdateRequest(ServerInfo server, Player player) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        out.writeUTF("Download");
        out.writeUTF(player.getUniqueId().toString());
        server.sendData("BungeeCord", b.toByteArray());
    }
}