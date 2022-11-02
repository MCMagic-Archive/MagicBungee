package us.mcmagic.magicbungee.socket.packets;

/**
 * Created by Marc on 6/15/15
 */
public enum PacketID {
    HEARTBEAT(0), LOGIN(1), KICK(2), GLOBAL_PLAY_ONCE(3), AREA_START(4), AREA_STOP(5), CLIENT_ACCEPTED(6), AUDIO_SYNC(7),
    NOTIFICATION(8), EXEC_SCRIPT(9), COMPUTER_SPEAK(10), INCOMING_WARP(11), SERVER_SWITCH(12);

    final int ID;

    PacketID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return this.ID;
    }

    public enum Bungee {
        GETPLAYER(13), PLAYERINFO(14), SENDMESSAGE(15), CHAT(16), CONTAINER(17);

        final int id;

        Bungee(int id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }
    }

    public enum Dashboard {
        STATUSREQUEST(18), SERVERSTATUS(19), CHATMESSAGE(20), CONTAINER(21);

        final int id;

        Dashboard(int id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }
    }
}