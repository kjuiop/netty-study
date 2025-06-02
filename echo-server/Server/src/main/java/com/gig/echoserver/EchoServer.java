package com.gig.echoserver;

public class EchoServer {

    public static void main(String []args) {
        int port = Config.getInt("server.port", 8080);
        System.out.println("server port: " + port);
    }
}
