package us.mcmagic.magicbungee.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.magicbungee.MagicBungee;
import us.mcmagic.magicbungee.socket.packets.BasePacket;
import us.mcmagic.magicbungee.socket.packets.as.PacketKick;
import us.mcmagic.magicbungee.socket.packets.bungee.PacketContainer;
import us.mcmagic.magicbungee.socket.server.WebSocketServerHandler;
import us.mcmagic.magicbungee.socket.server.WebSocketServerInitializer;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Marc on 5/22/16
 */
public class SocketConnection {
    private final int id;
    private ConcurrentHashMap<String, String> sessionKeys = new ConcurrentHashMap<>();

    public SocketConnection() {
        this.id = MagicBungee.getProxyServer().getScheduler().runAsync(MagicBungee.getInstance(), new Runnable() {

            public void run() {
                EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap b = new ServerBootstrap();
                    b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                            .childHandler(new WebSocketServerInitializer());
                    Channel ch = b.bind(4113).sync().channel();
                    MagicBungee.getInstance().getLogger().info("Socket Connections open on port 4113");
                    ch.closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }
        }).getId();
    }

    public void stop() {
        MagicBungee.getProxyServer().getScheduler().cancel(id);
    }

    public ConcurrentHashMap<String, String> getSessionKeys() {
        return this.sessionKeys;
    }

    public void generateAudioUrl(ProxiedPlayer player) {
        String key = generateSessionKey(player);
        BaseComponent[] msg = new ComponentBuilder("\nClick here to connect to our Audio Server!\n")
                .color(ChatColor.GREEN).underlined(true).bold(true).event((new ClickEvent(ClickEvent.Action.OPEN_URL,
                        "http://audio.mcmagic.us/?username=" + player.getName() + "&auth=" + key))).create();
        player.sendMessage(msg);
    }

    public String generateSessionKey(ProxiedPlayer player) {
        int sessonId = new Random().nextInt(100000);
        this.sessionKeys.remove(player.getName());
        this.sessionKeys.put(player.getName(), Integer.toString(sessonId));
        return Integer.toString(sessonId);
    }

    public void disconnect(ProxiedPlayer player) {
        PacketKick packet = new PacketKick("See ya real soon!");
        sendMessage(player, packet.getJSON().toString());
    }

    public void sendMessage(BasePacket packet) {
        if (packet.getId() > 17) {
            //Ignore Dashboard packets for now
            return;
        }
        for (Object o : WebSocketServerHandler.getGroup()) {
            Channel channel = (Channel) o;
            channel.writeAndFlush(new TextWebSocketFrame(packet.getJSON().toString()));
        }
    }

    public void sendMessage(ProxiedPlayer player, String s) {
        PacketContainer container = new PacketContainer(player.getUniqueId(), s);
        for (Object o : WebSocketServerHandler.getGroup()) {
            Channel channel = (Channel) o;
            channel.writeAndFlush(new TextWebSocketFrame(container.getJSON().toString()));
        }
    }
}