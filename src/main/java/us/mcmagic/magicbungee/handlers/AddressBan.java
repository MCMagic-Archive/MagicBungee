package us.mcmagic.magicbungee.handlers;

/**
 * Created by Marc on 3/15/15
 */
public class AddressBan {
    private String address;
    private String reason;
    private String source;

    public AddressBan(String address, String reason, String source) {
        this.address = address;
        this.reason = reason;
        this.source = source;
    }

    public String getAddress() {
        return address;
    }

    public String getReason() {
        return reason;
    }

    public String getSource() {
        return source;
    }
}
