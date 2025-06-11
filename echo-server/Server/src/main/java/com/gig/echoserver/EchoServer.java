package com.gig.echoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        // netty 의 쓰레드 풀
        // NioEventLoopGroup : 비동기 selector 를 기반으로 클라이언트 I/O 처리를 담당
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(this.port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    });

            // b.bind().sync() 는 서버 바인딩이 될 때까지 블로킹하여 완료될 때까지 기다림
            ChannelFuture f = b.bind().sync();
            System.out.println(EchoServer.class.getName() + " started and listening for connections on " + f.channel().localAddress());
            f.channel().closeFuture().sync();
        } catch (InterruptedException ie) {
            System.err.println("EchoServer interrupted, err : " + ie.getMessage());
            ie.printStackTrace(System.err);
        } finally {
            System.out.println(EchoServer.class.getName() + " gracefully shutdown ");
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String []args) throws InterruptedException {
        int port = Config.getInt("server.port", 8099);
        new EchoServer(port).start();
    }
}
