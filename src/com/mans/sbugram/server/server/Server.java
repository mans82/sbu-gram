package com.mans.sbugram.server.server;

import com.mans.sbugram.server.events.EventManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server extends Thread {

    private final EventManager eventManager;
    private final ServerSocket serverSocket;
    private boolean isRunning = true;
    private final ExecutorService executor = Executors.newFixedThreadPool(Server.THREAD_COUNT);

    private static final int THREAD_COUNT = 5;

    public Server(ServerSocket serverSocket, EventManager eventManager){
        this.serverSocket = serverSocket;
        this.eventManager = eventManager;
    }

    @Override
    public synchronized void start() {
        if (this.isRunning) {
            super.start();
        }
    }

    @Override
    public void run() {
        while (this.isRunning) {
            Socket incomingConnection;
            try {
                incomingConnection = this.serverSocket.accept();
            } catch (IOException e) {
                continue;
            }

            try {
                this.executor.submit(new ClientHandler(incomingConnection.getInputStream(), incomingConnection.getOutputStream(), eventManager));
            } catch (IOException ignored) {}
        }

        this.executor.shutdown();
        while (true) {
            try {
                if (this.executor.awaitTermination(1, TimeUnit.SECONDS)) {
                    break;
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public synchronized void stopServer() {
        this.isRunning = false;
    }
}