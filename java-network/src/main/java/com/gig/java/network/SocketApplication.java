package com.gig.java.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author : JAKE
 * @date : 2025/06/14
 */
public class SocketApplication {

    private static final int port = 8088;

    public static void main(String []args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            // accept() 호출로 연결될 때까지 진행을 블로킹한다.
            Socket clientSocket = serverSocket.accept();

            // 스트림 객체를 생성한다.
            // 문자 입력 스트림에서 텍스트를 읽는다.
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
            );
            // 객체의 포매팅된 표현을 테긋트 출력 스트림으로 출력한다.
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // 처리 루프를 시작한다.
            String request, response;
            // readLine() 을 통해 문자열을 읽을 때까지 진행을 블로킹한다.
            while ((request = in.readLine()) != null) {
                // 종료 신호가 들어오면 루프를 종료한다.
                if ("Done".equals(request)) {
                    break;
                }

                // client 요청을 처리한다.
                response = processRequest(request);
                out.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String processRequest(String request) {
        return request;
    }
}
