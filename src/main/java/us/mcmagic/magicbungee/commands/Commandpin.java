package us.mcmagic.magicbungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.MagicCommand;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.utils.ActivityUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class Commandpin extends MagicCommand {

    public Commandpin() {
        super(Rank.CASTMEMBER);
        aliases = Arrays.asList("mymcmagic");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }
        Player player = MagicBungee.getPlayer(((ProxiedPlayer) sender).getUniqueId());
        try (Connection connection = ActivityUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}