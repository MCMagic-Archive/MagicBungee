package us.mcmagic.magicbungee.socket.packets.bungee;

import com.google.gson.JsonObject;
import us.mcmagic.magicbungee.socket.packets.BasePacket;
import us.mcmagic.magicbungee.socket.packets.PacketID;

/**
 * Created by Marc on 6/15/15
 */
public class PacketGetPlayer extends BasePacket {
    private String playername = "";

    public PacketGetPlayer() {
        this("");
    }

    public PacketGetPlayer(String playername) {
        this.id = PacketID.Bungee.GETPLAYER.getID();
        this.playername = playername;
    }

    public String getPlayerName() {
        return this.playername;
    }

    public PacketGetPlayer fromJSON(JsonObject obj) {
        this.playername = obj.get("playername").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("playername", this.playername);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}