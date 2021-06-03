package com.mans.sbugram.server;

import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.events.EventManager;
import com.mans.sbugram.server.events.LoginEventHandler;
import com.mans.sbugram.server.events.SignUpEventHandler;
import com.mans.sbugram.server.server.Server;

import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) throws Exception{
        UserDao userDao = new UserDao("/tmp/sbugram/users");
        EventManager eventManager = new EventManager();

        eventManager.addEventHandler(new SignUpEventHandler(userDao));
        eventManager.addEventHandler(new LoginEventHandler(userDao));

        Server server = new Server(new ServerSocket(8228), eventManager);
        server.start();
    }
}
