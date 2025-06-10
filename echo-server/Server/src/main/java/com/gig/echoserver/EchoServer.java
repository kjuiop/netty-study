package com.gig.echoserver;

public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Starting server on port " + port);
    }

    public static void main(String []args) {
        int port = Config.getInt("server.port", 8080);
        new EchoServer(port).start();
    }
}
