package us.mcmagic.magicbungee.handlers;

import java.util.UUID;

/**
 * Created by Marc on 5/17/15
 */
public class Kick {
    private UUID uuid;
    private String reason;
    private String source;

    public Kick(UUID uuid, String reason, String source) {
        this.uuid = uuid;
        this.reason = reason;
        this.source = source;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getReason() {
        return reason;
    }

    public String getSource() {
        return source;
    }
}
