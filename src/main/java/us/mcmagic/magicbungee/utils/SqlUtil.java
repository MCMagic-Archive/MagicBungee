package us.mcmagic.magicbungee.utils;

import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.Kick;
import us.mcmagic.magicbungee.handlers.Mute;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.permissions.Rank;
import us.mcmagic.magicbungee.threads.StaffClock;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SqlUtil {
    protected static String connString;
    protected static String user;
    protected static String pass;
    protected static String myMCMagicConnString;

    public static void initialize() {
        connString = "jdbc:mysql://" + MagicBungee.config().getString("sql.ip") + ":" + MagicBungee.config().getString(
                "sql.port") + "/" + MagicBungee.config().getString("sql.database");
        myMCMagicConnString = "jdbc:mysql://" + MagicBungee.config().getString("sql.ip") + ":" + MagicBungee.config().getString(
                "sql.port") + "/mymcmagic";
        user = MagicBungee.config().getString("sql.username");
        pass = MagicBungee.config().getString("sql.password");
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(connString, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void logKick(Kick kick) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO kicks (uuid, reason, source) VALUES (?,?,?)");
            sql.setString(1, kick.getUniqueId().toString());
            sql.setString(2, kick.getReason());
            sql.setString(3, kick.getSource());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Player createPlayer(UUID uuid, String name, String ipAddress) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM player_data WHERE uuid=?");
            sql.setString(1, uuid.toString());
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                result.close();
                sql.close();
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return setupPlayer(uuid, name, ipAddress);
            }
            String user = result.getString("username");
            if (!user.equals(name)) {
                result.close();
                sql.close();
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return updateData(uuid, user, name, ipAddress);
            }
            Mute mute = MuteUtil.getMute(uuid, name);
            String ip = result.getString("ipAddress");
            Player player = new Player(uuid, name, Rank.fromString(result.getString("rank")), result.getInt("toggled") == 1,
                    mute, ipAddress, result.getInt("mentions") == 1);
            result.close();
            sql.close();
            PreparedStatement sql2 = connection.prepareStatement("UPDATE player_data SET lastseen=? WHERE uuid=?");
            sql2.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            sql2.setString(2, uuid.toString());
            sql2.execute();
            sql2.close();
            if (!ip.equals(ipAddress)) {
                updateIPAddress(uuid, ipAddress);
            }
            return player;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateIPAddress(UUID uuid, String ipAddress) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET ipAddress=? WHERE uuid=?");
            sql.setString(1, ipAddress);
            sql.setString(2, uuid.toString());
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getIP(String uuid) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM player_data WHERE uuid=?");
            sql.setString(1, uuid);
            ResultSet result = sql.executeQuery();
            result.next();
            String ip = result.getString("ipAddress");
            result.close();
            sql.close();
            return ip;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean containsPlayer(String username) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT * FROM player_data WHERE username=?");
            sql.setString(1, username);
            ResultSet result = sql.executeQuery();
            boolean contains = result.next();
            result.close();
            sql.close();
            return contains;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean exists(String username) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM player_data WHERE username=?");
            sql.setString(1, username);
            ResultSet result = sql.executeQuery();
            boolean containsPlayer = result.next();
            sql.close();
            result.close();
            return containsPlayer;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String uuidFromUsername(String username) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("SELECT uuid FROM player_data WHERE username=?");
            sql.setString(1, username);
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                result.close();
                sql.close();
                return "";
            }
            String uuid = result.getString("uuid");
            sql.close();
            result.close();
            return uuid;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String usernameFromUUID(String uuid) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT username FROM player_data WHERE uuid=?");
            sql.setString(1, uuid);
            ResultSet result = sql.executeQuery();
            if (!result.next()) {
                result.close();
                sql.close();
                return "";
            }
            String username = result.getString("username");
            result.close();
            sql.close();
            return username;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static Player setupPlayer(UUID uuid, String username, String ipAddress) {
        try (Connection connection = getConnection()) {
            PreparedStatement check = connection.prepareCall("SELECT * FROM player_data WHERE username=?");
            check.setString(1, username);
            ResultSet r = check.executeQuery();
            while (r.next()) {
                List<String> names = NameUtil.getNames(r.getString("uuid").replace("-", ""));
                if (names.isEmpty()) {
                    break;
                }
                PreparedStatement update = connection.prepareCall("UPDATE player_data SET username=? WHERE uuid=?");
                update.setString(1, names.get(names.size() - 1));
                update.setString(2, r.getString("uuid"));
                update.execute();
                update.close();
            }
            r.close();
            check.close();
            PreparedStatement sql = connection.prepareStatement("INSERT INTO player_data " +
                    "(uuid, username, friends, requests, ipAddress) VALUES('" + uuid.toString() + "', '" + username + "', '', '', '" +
                    ipAddress + "');");
            /*
            sql.setString(1, uuid.toString());
            sql.setString(2, username);
            sql.setString(3, "");
            sql.setString(4, "");
            sql.setInt(5, 0);
            sql.setString(6, "guest");
            sql.setInt(7, 0);
            sql.setInt(8, 0);
            sql.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            sql.setString(10, "Hub");
            sql.setString(11, ipAddress);
            sql.setString(12, "blue");
            sql.setString(13, "orange");
            */
            sql.execute();
            sql.close();
            return new Player(uuid, username, Rank.GUEST, true, new Mute(uuid, username, false,
                    System.currentTimeMillis(), "", ""), ipAddress, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Player updateData(UUID uuid, String oldUsername, String username, String ipAddress) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET username=?,lastseen=?,ipAddress=? " +
                    "WHERE uuid=?");
            sql.setString(1, username);
            sql.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            sql.setString(3, ipAddress);
            sql.setString(4, uuid.toString());
            sql.execute();
            sql.close();
            PreparedStatement second = connection.prepareStatement("SELECT * FROM player_data WHERE uuid=?");
            second.setString(1, uuid.toString());
            ResultSet result = second.executeQuery();
            if (!result.next()) {
                return null;
            }
            Player player = new Player(UUID.fromString(result.getString("uuid")), username,
                    Rank.fromString(result.getString("rank")), result.getInt("toggled") == 1,
                    MuteUtil.getMute(UUID.fromString(result.getString("uuid")), username),
                    result.getString("ipAddress"), result.getInt("mentions") == 1);
            result.close();
            second.close();
            return player;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getNamesFromIP(String ipAddress) {
        List<String> list = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT * FROM player_data WHERE ipAddress=?");
            sql.setString(1, ipAddress);
            ResultSet result = sql.executeQuery();
            while (result.next()) {
                list.add(result.getString("username"));
            }
            result.close();
            sql.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date lastSeen(String uuid) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT lastseen FROM player_data WHERE uuid=?");
            sql.setString(1, uuid);
            ResultSet result = sql.executeQuery();
            result.next();
            Date time = result.getTimestamp("lastseen");
            result.close();
            sql.close();
            return time;
        } catch (Exception e) {
            e.printStackTrace();
            return new Date(System.currentTimeMillis());
        }
    }

    public static void setServer(String uuid, String server) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET server=? WHERE uuid=?");
            sql.setString(1, server);
            sql.setString(2, uuid);
            sql.execute();
            sql.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logout(Player player) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection.prepareStatement("UPDATE player_data SET server=?,lastseen=?,onlinetime = onlinetime+? WHERE uuid=?");
            if (player.getServer() != null) {
                sql.setString(1, player.getServer().getInfo().getName());
            } else {
                sql.setString(1, "Not available");
            }
            sql.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            sql.setInt(3, (int) ((System.currentTimeMillis() / 1000) - (player.getLoginTime() / 1000)));
            sql.setString(4, player.getUniqueId().toString());
            sql.execute();
            sql.close();
            if (player.getRank().getRankId() >= Rank.EARNINGMYEARS.getRankId()) {
                new StaffClock(player.getUniqueId(), "logout", System.currentTimeMillis() / 1000).run();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getServer(String uuid) {
        try (Connection connection = getConnection()) {
            PreparedStatement sql = connection
                    .prepareStatement("SELECT server FROM player_data WHERE uuid=?");
            sql.setString(1, uuid);
            ResultSet result = sql.executeQuery();
            result.next();
            String server = result.getString("server");
            result.close();
            sql.close();
            return server;
        } catch (Exception e) {
            e.printStackTrace();
            return "TTC";
        }
    }
}