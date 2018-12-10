package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        startServerSocket();
    }

    public static void startServerSocket() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(3000);
            while (true) {
                System.out.println("waiting");
                Socket socket = serverSocket.accept();
                System.out.println("connected");
                new ClientThread(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null)
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
