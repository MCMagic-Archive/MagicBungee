package us.mcmagic.magicbungee.socket.packets.dashboard;

import com.google.gson.JsonObject;
import us.mcmagic.magicbungee.socket.packets.BasePacket;
import us.mcmagic.magicbungee.socket.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 5/30/16
 */
public class PacketChatMessage extends BasePacket {
    private UUID uuid;
    private String username;
    private String message;
    private boolean park;
    private String server;
    private boolean privateMessage;

    public PacketChatMessage() {
        this(null, "", "", false, "", false);
    }

    public PacketChatMessage(UUID uuid, String username, String message, boolean park, String server,
                             boolean privateMessage) {
        this.id = PacketID.Dashboard.CHATMESSAGE.getID();
        this.uuid = uuid;
        this.username = username;
        this.message = message.trim();
        this.park = park;
        this.server = server;
        this.privateMessage = privateMessage;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public boolean isPark() {
        return park;
    }

    public String getServer() {
        return server;
    }

    public boolean isPrivateMessage() {
        return privateMessage;
    }

    public PacketChatMessage fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.username = obj.get("username").getAsString();
        this.message = obj.get("message").getAsString();
        this.park = obj.get("park").getAsBoolean();
        this.server = obj.get("server").getAsString();
        this.privateMessage = obj.get("privateMessage").getAsBoolean();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            obj.addProperty("username", this.username);
            obj.addProperty("message", this.message);
            obj.addProperty("park", this.park);
            obj.addProperty("server", this.server);
            obj.addProperty("privateMessage", this.privateMessage);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}