package us.mcmagic.magicbungee.threads;

import us.mcmagic.magicbungee.utils.SqlUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Marc on 8/21/15
 */
public class StaffClock implements Runnable {
    private UUID uuid;
    private String action;
    private long time;

    public StaffClock(UUID uuid, String action, long time) {
        this.uuid = uuid;
        this.action = action;
        this.time = time;
    }

    @Override
    public void run() {
        try (Connection connection = SqlUtil.getConnection()) {
            PreparedStatement sql = connection.prepareStatement("INSERT INTO staffclock (id, user, action, time) " +
                    "VALUES(0, ?, ?, ?)");
            sql.setString(1, uuid.toString());
            sql.setString(2, action);
            sql.setLong(3, time);
            sql.execute();
            sql.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}