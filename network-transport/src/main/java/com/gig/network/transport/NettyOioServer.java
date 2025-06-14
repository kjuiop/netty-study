package com.gig.network.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @author : JAKE
 * @date : 2025/06/14
 */
public class NettyOioServer {

    public void server(int port) throws InterruptedException {
        // ByteBuf : Netty 의 데이터 버퍼
        // 문자열을 UTF-8 바이트 배열로 변환
        // unReleaseBuffer : 버퍼에 메모리 해제를 막아 여러 클라이언트에서 공유 가능
        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n", StandardCharsets.UTF_8));
        // oio EventLoopGroup 으로 블로킹 방식의 핸들러 사용
        EventLoopGroup group = new OioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            // OioEventLoopGroup 을 이용해 블로킹 모드를 허용 (OIO)
            b.group(group).channel(OioServerSocketChannel.class).localAddress(
                    new InetSocketAddress(port))
                    // 연결이 수락될 때마다 channelInitializer 수행
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch)
                        throws Exception {
                    // ch.pipeline() 에 핸들러 등록
                    // 이벤트를 가로채고 처리할 ChannelInboundHandlerAdapter 추가
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                        // channelActive 이벤트 호출되는 순간은 연결이 성공한 순간
                        @Override
                        public void channelActive(ChannelHandlerContext ctx)
                                throws Exception {
                            // 기존 버퍼의 읽기/쓰기 인덱스만 복제한 후 버퍼를 생성
                            // duplicate 는 새로운 ByteBuf 를 반환
                            // 메시지가 출력되면 채널을 닫음
                            ctx.write(buf.duplicate()).addListener(ChannelFutureListener.CLOSE);
                        }
                    });
                }
            });
            // 서버를 바인딩해 연결을 수락
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
