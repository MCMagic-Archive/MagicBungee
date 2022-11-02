package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Marc on 12/17/15
 */
public class Commandapply extends MagicCommand {
    private SecureRandom random = new SecureRandom();
    private static String pass = MagicBungee.config().getString("site-sql-password");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://mcmagic1:3306/newsite", "root", pass);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }
        final Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        player.sendMessage(new ComponentBuilder("\nPreparing your Application now...").color(ChatColor.GREEN).create());
        MagicBungee.getProxyServer().getScheduler().runAsync(MagicBungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                String token = secureToken();
                try (Connection connection = getConnection()) {
                    PreparedStatement delete = connection.prepareStatement("DELETE FROM apptokens WHERE uuid=?");
                    delete.setString(1, player.getUniqueId().toString());
                    delete.execute();
                    delete.close();
                    PreparedStatement sql = connection.prepareStatement("INSERT INTO apptokens (uuid,token) VALUES (?,?)");
                    sql.setString(1, player.getUniqueId().toString());
                    sql.setString(2, token);
                    sql.execute();
                    sql.close();
                    player.sendMessage(new ComponentBuilder("\nYour Application is ready! ").color(ChatColor.GREEN)
                            .append("Click Here to visit the website.\n").color(ChatColor.AQUA)
                            .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://mcmagic.us/apply/?uuid=" +
                                    player.getUniqueId().toString() + "&token=" + token)).event(
                                    new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            new ComponentBuilder("Click to visit https://mcmagic.us/apply")
                                                    .color(ChatColor.GREEN).create())).create());
                } catch (SQLException e) {
                    e.printStackTrace();
                    player.sendMessage(new ComponentBuilder("An error occured while preparing your Application. Try again later!")
                            .color(ChatColor.RED).create());
                }
            }
        });
    }

    private String secureToken() {
        return new BigInteger(130, random).toString(32);
    }

}