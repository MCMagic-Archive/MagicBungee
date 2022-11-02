package us.mcmagic.magicbungee.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Marc on 7/5/16
 */
public class ActivityUtil {

    public static void logActivity(UUID uuid, String action, String description) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO activity (uuid, action, description) VALUES (?,?,?)");
            sql.setString(1, uuid.toString());
            sql.setString(2, action);
            sql.setString(3, description);
            sql.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(SqlUtil.myMCMagicConnString, SqlUtil.user, SqlUtil.pass);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}