package us.mcmagic.magicbungee.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.magicbungee.MagicBungee;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 1/16/15
 */
public class ProxyPing implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        List<String> info = new ArrayList<>(MagicBungee.info);
        ServerPing.PlayerInfo[] infolist = new ServerPing.PlayerInfo[info.size()];
        for (int i = 0; i < info.size(); i++) {
            infolist[i] = new ServerPing.PlayerInfo(ChatColor.translateAlternateColorCodes('&', info.get(i)), "");
        }
        ServerPing response = event.getResponse();
        if (MagicBungee.maintenance) {
            event.setResponse(new ServerPing(response.getVersion(), new ServerPing.Players(0, 0, infolist),
                    ChatColor.translateAlternateColorCodes('&', MagicBungee.motdmaintenance),
                    response.getFaviconObject()));
        } else {
            event.setResponse(new ServerPing(response.getVersion(), new ServerPing.Players(2000,
                    ProxyServer.getInstance().getOnlineCount(), infolist), ChatColor.translateAlternateColorCodes('&',
                    MagicBungee.motd), response.getFaviconObject()));
        }
    }
}