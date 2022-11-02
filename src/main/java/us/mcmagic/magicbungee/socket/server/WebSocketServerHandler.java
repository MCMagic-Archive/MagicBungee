package us.mcmagic.magicbungee.socket.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.concurrent.GlobalEventExecutor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.handlers.Player;
import us.mcmagic.magicbungee.socket.packets.bungee.PacketChat;
import us.mcmagic.magicbungee.socket.packets.bungee.PacketGetPlayer;
import us.mcmagic.magicbungee.socket.packets.bungee.PacketPlayerInfo;
import us.mcmagic.magicbungee.socket.packets.bungee.PacketSendMessage;
import us.mcmagic.magicbungee.socket.packets.dashboard.PacketServerStatus;
import us.mcmagic.magicbungee.utils.ChatUtil;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Marc on 5/22/16
 */
public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {

    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private WebSocketServerHandshaker handshaker;

    public static ChannelGroup getGroup() {
        return channels;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        channels.add(ctx.channel());
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://mcmagic.us:8887", null, true);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if (frame instanceof PongWebSocketFrame) {
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }
        String request = ((TextWebSocketFrame) frame).text();
        JsonObject object = (JsonObject) new JsonParser().parse(request);
        if (!object.has("id")) {
            return;
        }
        int id = object.get("id").getAsInt();
        switch (id) {
            case 13: {
                PacketGetPlayer packet = new PacketGetPlayer().fromJSON(object);
                String username = packet.getPlayerName();
                Player player = MagicBungee.getPlayer(username);
                PacketPlayerInfo info;
                if (player == null) {
                    info = new PacketPlayerInfo(null, username, 0, "");
                } else {
                    String key = MagicBungee.socketConnection.getSessionKeys().get(username);
                    if (!isInt(key)) {
                        break;
                    }
                    info = new PacketPlayerInfo(player.getUniqueId(), username, Integer.valueOf(key), player.getServerName());
                }
                ctx.writeAndFlush(new TextWebSocketFrame(info.getJSON().toString()));
                break;
            }
            case 15: {
                PacketSendMessage packet = new PacketSendMessage().fromJSON(object);
                UUID uuid = packet.getUniqueId();
                String msg = packet.getMessage();
                Player tp = MagicBungee.getPlayer(uuid);
                if (tp == null) {
                    break;
                }
                if (msg.contains("connected")) {
                    MagicBungee.getProxyServer().getPlayer(tp.getUniqueId());
                    try {
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(b);
                        out.writeUTF("AudioServerConnect");
                        out.writeUTF(tp.getUniqueId().toString());
                        tp.getServer().sendData("BungeeCord", b.toByteArray());
                    } catch (IOException ignored) {
                    }
                } else if (msg.contains("left")) {
                    MagicBungee.getProxyServer().getPlayer(tp.getUniqueId());
                    try {
                        ByteArrayOutputStream b = new ByteArrayOutputStream();
                        DataOutputStream out = new DataOutputStream(b);
                        out.writeUTF("AudioServerDisconnect");
                        out.writeUTF(tp.getUniqueId().toString());
                        tp.getServer().sendData("BungeeCord", b.toByteArray());
                    } catch (IOException ignored) {
                    }
                }
                tp.sendMessage(ChatUtil.colorize(msg));
                break;
            }
            case 16: {
                PacketChat packet = new PacketChat().fromJSON(object);
                UUID uuid = packet.getUniqueId();
                String message = packet.getMessage();
                ProxiedPlayer tp = MagicBungee.getProxyServer().getPlayer(uuid);
                if (tp == null) {
                    break;
                }
                tp.chat(message);
                break;
            }
            case 18: {
                JsonArray array = new JsonArray();
                for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
                    JsonObject s = new JsonObject();
                    s.addProperty("name", server.getName());
                    s.addProperty("park", ChatUtil.getParkChatServers().contains(server.getName()));
                    s.addProperty("count", server.getPlayers().size());
                    array.add(s);
                }
                PacketServerStatus packet = new PacketServerStatus(MagicBungee.getProxyServer().getOnlineCount(), array);
                ctx.writeAndFlush(new TextWebSocketFrame(packet.getJSON().toString()));
            }
        }
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }
}
