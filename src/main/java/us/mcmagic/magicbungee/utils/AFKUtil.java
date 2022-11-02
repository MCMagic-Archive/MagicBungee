package us.mcmagic.magicbungee.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Marc on 5/10/15
 */
public class AFKUtil {

    public AFKUtil() {
        MagicBungee.getProxyServer().getScheduler().schedule(MagicBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player tp : MagicBungee.getOnlinePlayers()) {
                    if (tp.getRank().getRankId() < Rank.EARNINGMYEARS.getRankId() || tp.getRank().getRankId() >=
                            Rank.DEVELOPER.getRankId()) {
                        continue;
                    }
                    if (System.currentTimeMillis() - tp.getAfkTime() >= 1800000) {
                        if (tp.hasCaptcha()) {
                            continue;
                        }
                        try {
                            warn(tp);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void warn(final Player player) throws IOException {
        final UUID uuid = player.getUniqueId();
        BaseComponent[] afk = new ComponentBuilder("                      AFK Timer:").color(ChatColor.RED)
                .bold(true).create();
        BaseComponent[] blank = new ComponentBuilder("").create();
        BaseComponent[] msg = new ComponentBuilder("CLICK HERE").color(ChatColor.YELLOW).bold(true)
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afkcaptcha " + player.createAfkCaptcha()))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("CLICK HERE").color(ChatColor.GOLD)
                        .bold(true).create())).create();
        final UUID captcha = player.getCaptcha();
        final List<BaseComponent[]> msgs = Arrays.asList(blank, blank, afk, blank, msg, blank, blank, blank, blank, blank);
        final int id = MagicBungee.getProxyServer().getScheduler().schedule(MagicBungee.getInstance(), new Runnable() {
            int i = 0;

            @Override
            public void run() {
                try {
                    Player tp = MagicBungee.getPlayer(uuid);
                    if (tp.getCaptcha().equals(captcha)) {
                        Title title = MagicBungee.getProxyServer().createTitle();
                        title.fadeIn(10);
                        title.stay(1200);
                        title.fadeOut(20);
                        title.title(new ComponentBuilder("Are you AFK?").color(ChatColor.RED).bold(true).create());
                        title.subTitle(new ComponentBuilder("Click below, AFK kick in ").color(ChatColor.RED).append((5 - i) + " ")
                                .color(ChatColor.DARK_RED).append("minutes!").color(ChatColor.RED).create());
                        title.send(MagicBungee.getProxyServer().getPlayer(tp.getUniqueId()));
                        i++;
                        for (BaseComponent[] m : msgs) {
                            tp.sendMessage(m);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }, 0L, 1L, TimeUnit.MINUTES).getId();
        MagicBungee.getProxyServer().getScheduler().schedule(MagicBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                Player tp = MagicBungee.getPlayer(uuid);
                MagicBungee.getProxyServer().getScheduler().cancel(id);
                try {
                    if (tp.getCaptcha().equals(captcha)) {
                        tp.kickPlayer(new ComponentBuilder("You have been AFK for 30 minutes. Please try not to be AFK while on our servers.")
                                .color(ChatColor.RED).create());
                        try (Connection connection = SqlUtil.getConnection()) {
                            PreparedStatement sql = connection.prepareStatement("INSERT INTO afklogs (`user`) VALUES('" +
                                    uuid + "')");
                            sql.execute();
                            sql.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }, 5, TimeUnit.MINUTES);
    }
}
