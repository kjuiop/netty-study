package com.gig.network.transport;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * @author : JAKE
 * @date : 2025/06/14
 */
// 블로킹 네트워킹
public class PlainOioServer {

    public void serve(int port) {

        try {
            final ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                // accept 는 블로킹 메서드로 클라이언트의 연결을 기다립니다.
                final Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket);

                // client 연결이 들어올 때마다 쓰레드를 생성해서 처리
                // 요청에 대한 Thread 가 1:1로 매핑
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream out;
                        try {
                            out = clientSocket.getOutputStream();
                            out.write("Hi!\r\n".getBytes(Charset.forName("UTF-8")));
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
