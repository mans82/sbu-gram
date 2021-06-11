package com.mans.sbugram.server;

import com.mans.sbugram.server.dao.impl.UploadedFileDao;
import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.events.*;
import com.mans.sbugram.server.server.Server;

import java.net.ServerSocket;

public class Main {
    public static void main(String[] args) throws Exception{
        UserDao userDao = new UserDao("/tmp/sbugram/users");
        UploadedFileDao fileDao = new UploadedFileDao("/tmp/sbugram/uploaded");

        EventManager eventManager = new EventManager();

        eventManager.addEventHandler(new SignUpEventHandler(userDao));
        eventManager.addEventHandler(new LoginEventHandler(userDao));
        eventManager.addEventHandler(new FileUploadEventHandler(fileDao));
        eventManager.addEventHandler(new FileDownloadEventHandler(fileDao));

        Server server = new Server(new ServerSocket(8228), eventManager);
        server.start();
    }
}
