package com.gig.echoserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class EchoClient {

    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws InterruptedException {
        System.out.println("Starting EchoClient");
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });

            // 서버에 연결될 때까지 기다리는 구간
            // 서버에 연결될 때까지 기다린 다음, 연결된 채널이 닫힐 때까지 프로그램을 종료하지 않고 대기한다.
            ChannelFuture f = b.connect().sync();
            f.channel().closeFuture().sync();
        } catch(InterruptedException ie) {
            System.err.println("EchoServer interrupted, err : " + ie.getMessage());
            ie.printStackTrace(System.err);
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String host = Config.getString("server.host", "localhost");
        int port = Config.getInt("server.port", 8098);
        new EchoClient(host, port).start();
    }
}
