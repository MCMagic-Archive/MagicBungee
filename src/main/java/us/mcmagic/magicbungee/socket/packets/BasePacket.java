package us.mcmagic.magicbungee.socket.packets;

import com.google.gson.JsonObject;

/**
 * Created by Marc on 6/15/15
 */
public class BasePacket {
    protected int id = 0;

    public int getId() {
        return id;
    }

    public BasePacket fromJSON(JsonObject obj) {
        return this;
    }

    public JsonObject getJSON() {
        return null;
    }
}