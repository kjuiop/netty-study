package com.gig.echoserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    // 메시지가 들어올 때마다 호출되는 함수
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        byte[] b = new byte[in.readableBytes()];
        in.getBytes(0, b, 0, in.readableBytes());
        String s = new String(b);
        //System.out.println("Server received: " + ByteBufUtil.hexDump(in));
        System.out.println("Server received: " + s);
        ctx.write(in);
    }
    // 클라이언트가 데이터를 전송하면 Netty가 이 메서드를 자동으로 호출.
    // ctx는 채널의 파이프라인 컨텍스트로, 이벤트를 다음 핸들러로 넘기거나 응답을 보낼 때 사용.
    // msg는 실제로 클라이언트로부터 받은 메시지 객체이며, 일반적으로 Netty에서는 ByteBuf 타입으로 전달됨.


    // channelRead() 의 마지막 호출에서 현재 일괄 처리의 마지막 메시지를 처리했음을 핸들러에 통보한다.
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    // 읽기 작업 중 예외가 발생하면 호출됨
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
