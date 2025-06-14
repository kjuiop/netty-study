package com.gig.network.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author : JAKE
 * @date : 2025/06/14
 */
public class PlainNioServer {

    public void serve(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        // Non-blocking 모드로 설정
        serverChannel.configureBlocking(false);
        ServerSocket serverSocket = serverChannel.socket();
        // 서버 port 바인딩
        InetSocketAddress address = new InetSocketAddress(port);
        serverSocket.bind(address);
        // 채널을 처리할 셀렉터 생성
        Selector selector = Selector.open();
        // 서버 채널에 셀렉터 연동
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        final ByteBuffer msg = ByteBuffer.wrap("Hi!\r\n".getBytes());
        while (true) {
            try {
                // 처리할 새로운 이벤트를 기다리고 다음 들어오는 이벤트까지 블로킹
                selector.select();
            } catch (IOException ex) {
                ex.printStackTrace();
                break;
            }

            // 이벤트를 수신한 모든 키들을 조회
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    // 이벤트가 수락할 수 있는 새로운 연결인지 확인
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        // 클라이언트를 수락하고 셀렉터에 등록
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate());
                        System.out.println("Accepted connection from " + client);
                    }
                    // 소켓에 데이터를 기록할 수 있는지 확인
                    if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        // 연결된 클라이언트의 데이터를 출력
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        while (buffer.hasRemaining()) {
                            // 버퍼가 모두 비워질때까지 write
                            if (client.write(buffer) == 0) {
                                break;
                            }
                        }
                        client.close();
                    }
                } catch (IOException ex) {
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException cex) {
                        cex.printStackTrace();
                    }
                }
            }
        }
    }

}
