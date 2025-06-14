package com.gig.network.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author : JAKE
 * @date : 2025/06/14
 */
public class NettyNioServer {

    public void server(int port) throws InterruptedException {
        // unReleaseBuffer : 버퍼에 메모리 해제를 막아 여러 클라이언트에서 공유 가능
        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n", StandardCharsets.UTF_8));
        // 논블로킹 모드를 위해 NioEventLoopGroup 을 이용
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            // server bootstrap 생성
            ServerBootstrap b = new ServerBootstrap();
            // nio socket channel 생성
            b.group(group).channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    // 연결이 수락될 때마다 channel initializer 생성
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                                      @Override
                                      public void initChannel(SocketChannel ch) throws Exception {
                                          // 이벤트를 수신하고 처리할 ChannelInboundHandler Adapter 추가
                                          ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                              @Override
                                              public void channelActive(ChannelHandlerContext ctx)
                                                      throws Exception {
                                                  // 메시지를 출력되면 연결을 닫음
                                                  // buf.duplicate()는 각 클라이언트에게 독립적인 readerIndex/view 를 제공하며, 데이터는 공유됨
                                                  ctx.writeAndFlush(buf.duplicate()).addListener(ChannelFutureListener.CLOSE);
                                              }
                                          });
                                      }
                                  }
                    );
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
