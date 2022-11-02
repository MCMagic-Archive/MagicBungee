package us.mcmagic.magicbungee.socket.packets.bungee;

import com.google.gson.JsonObject;
import us.mcmagic.magicbungee.socket.packets.BasePacket;
import us.mcmagic.magicbungee.socket.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 5/23/16
 */
public class PacketSendMessage extends BasePacket {
    private UUID uuid;
    private String message;

    public PacketSendMessage() {
        this(null, "");
    }

    public PacketSendMessage(UUID uuid, String message) {
        this.id = PacketID.Bungee.SENDMESSAGE.getID();
        this.uuid = uuid;
        this.message = message;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getMessage() {
        return message;
    }

    public PacketSendMessage fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        this.message = obj.get("message").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", uuid != null ? uuid.toString() : null);
            obj.addProperty("message", this.message);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}