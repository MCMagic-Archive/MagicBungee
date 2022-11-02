package us.mcmagic.magicbungee.utils;

import net.md_5.bungee.api.plugin.Listener;
import us.mcmagic.magicbungee.handlers.AddressBan;
import us.mcmagic.magicbungee.handlers.Ban;

import java.sql.*;
import java.util.Date;
import java.util.UUID;

public class BanUtil implements Listener {

    public static boolean isBannedPlayer(UUID uuid) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM banned_players WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet results = sql.executeQuery();
            boolean banned = false;
            while (results.next()) {
                if (results.getInt("active") == 1) {
                    banned = true;
                    break;
                }
            }
            sql.close();
            results.close();
            return banned;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isBannedIP(String ip) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM banned_ips WHERE ipAddress=?");
            sql.setString(1, ip);
            ResultSet resultset = sql.executeQuery();
            boolean isBanned = resultset.next();
            sql.close();
            resultset.close();
            return isBanned;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String banReasonPlayer(UUID uuid) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT reason FROM banned_players WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            String reason = null;
            while (result.next()) {
                if (result.getInt("active") == 0) {
                    continue;
                }
                reason = result.getString("reason");
            }
            result.close();
            sql.close();
            if (reason == null) {
                return "";
            }
            return reason;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String banReasonIP(String ip) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT reason FROM banned_ips WHERE ipAddress=?");
            sql.setString(1, ip);
            ResultSet result = sql.executeQuery();
            result.next();
            String reason = result.getString("reason");
            result.close();
            sql.close();
            return reason;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isTemporarilyBanned(UUID uuid) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT permanent FROM banned_players WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            result.next();
            int value = result.getInt("permanent");
            result.close();
            sql.close();
            return value != 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Date tempBanRelease(UUID uuid) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM banned_players WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            Date date = null;
            while (result.next()) {
                if (result.getInt("active") == 0) {
                    continue;
                }
                date = result.getTimestamp("release");
            }
            result.close();
            sql.close();
            if (date == null) {
                return new Date();
            }
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void banIP(String ip, String reason, String source) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO banned_ips values(0,?,?,?,1)");
            sql.setString(1, ip);
            sql.setString(2, reason);
            sql.setString(3, source);
            sql.execute();
            sql.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void banPlayer(UUID uuid, String reason, boolean permanent, Date release, String source) {
        Timestamp rts = new Timestamp(release.getTime());
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO banned_players values(0,?,?,?,?,?,1)");
            sql.setString(1, uuid.toString());
            sql.setString(2, reason);
            if (permanent) {
                sql.setInt(3, 1);
            } else {
                sql.setInt(3, 0);
            }
            sql.setTimestamp(4, rts);
            sql.setString(5, source);
            sql.execute();
            sql.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setReason(UUID uuid, String reason) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE banned_players SET reason=? WHERE uuid=? AND active=1");
            sql.setString(1, reason);
            sql.setString(2, uuid.toString());
            sql.execute();
            sql.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateTempBan(UUID uuid, String reason, Date release, String source) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE banned_players SET reason=?,release=?,source=?" +
                    " WHERE uuid=?");
            sql.setString(1, reason);
            sql.setTimestamp(2, new Timestamp(release.getTime()));
            sql.setString(3, source);
            sql.setString(4, uuid.toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static AddressBan getAddressBan(String ipAddress) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM banned_ips WHERE ipAddress=?");
            sql.setString(1, ipAddress);
            ResultSet result = sql.executeQuery();
            AddressBan ban = null;
            while (result.next()) {
                if (result.getInt("active") == 0) {
                    continue;
                }
                ban = new AddressBan(ipAddress, result.getString("reason"), result.getString("source"));
                result.close();
                sql.close();
                return ban;
            }
            if (ban == null) {
                result.close();
                sql.close();
                return ban;
            }
            result.close();
            sql.close();
            return ban;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Ban getBan(UUID uuid, String name) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM banned_players WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            Ban ban = null;
            while (result.next()) {
                if (result.getInt("active") == 0) {
                    continue;
                }
                ban = new Ban(uuid, name, result.getInt("permanent") == 1, result.getTimestamp("release").getTime(),
                        result.getString("reason"), result.getString("source"));
                result.close();
                sql.close();
                return ban;
            }
            if (ban == null) {
                result.close();
                sql.close();
                return null;
            }
            result.close();
            sql.close();
            return ban;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void unbanPlayer(UUID uuid) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE banned_players SET active=0 WHERE uuid=?");
            sql.setString(1, uuid.toString());
            sql.execute();
            sql.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unbanIP(String ip) {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE banned_ips SET active=0 WHERE ipAddress=?");
            sql.setString(1, ip);
            sql.execute();
            sql.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}