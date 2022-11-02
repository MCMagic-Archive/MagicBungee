package us.mcmagic.magicbungee.utils;

import us.mcmagic.magicbungee.handlers.Mute;

import java.sql.*;
import java.util.Date;
import java.util.UUID;

public class MuteUtil {

    public static boolean isMuted(UUID uuid) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM muted_players WHERE uuid=? AND active=1");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                sql.close();
                result.close();
                return false;
            }
            sql.close();
            result.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void mutePlayer(UUID uuid, Date release, String reason, String source) {
        Timestamp rts = new Timestamp(release.getTime());
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO muted_players values(0,?,?,?,?,1)");
            sql.setString(1, uuid.toString());
            sql.setTimestamp(2, rts);
            sql.setString(3, source);
            sql.setString(4, reason);
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void unmutePlayer(UUID uuid) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE muted_players SET active=0 WHERE uuid=?");
            sql.setString(1, uuid.toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Date muteRelease(UUID uuid) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM muted_players WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            result.next();
            Timestamp timestamp = result.getTimestamp("release");
            result.close();
            sql.close();
            return timestamp;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Mute getMute(UUID uuid, String username) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM muted_players WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            Mute mute = null;
            while (result.next()) {
                if (result.getInt("active") == 1) {
                    mute = new Mute(uuid, username, true, result.getTimestamp("release").getTime(),
                            result.getString("reason"), result.getString("source"));
                }
            }
            if (mute == null) {
                result.close();
                sql.close();
                return new Mute(uuid, username, false, System.currentTimeMillis(), "", "");
            }
            result.close();
            sql.close();
            return mute;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}