package com.mans.sbugram.server.server;

import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.events.EventManager;
import com.mans.sbugram.server.events.LoginEventHandler;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.factories.ResponseFactory;
import com.mans.sbugram.models.requests.LoginRequest;
import com.mans.sbugram.models.responses.LoginResponse;
import com.mans.sbugram.models.responses.Response;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServerTest {

    @Test(timeout = 2000)
    public void testLoginRequestCorrectCredentials() throws Exception {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                new LoginRequest("jafar", "1234")
                .toJSON().toString()
                .getBytes(StandardCharsets.UTF_8)
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Socket mockSocket = mock(Socket.class);
        when(mockSocket.getInputStream())
                .thenReturn(inputStream);
        when(mockSocket.getOutputStream())
                .thenReturn(outputStream);

        ServerSocket mockServerSocket = mock(ServerSocket.class);
        when(mockServerSocket.accept())
                .thenReturn(mockSocket);

        UserDao mockDao = mock(UserDao.class);
        when(mockDao.get("jafar"))
                .thenReturn(Optional.of(new User("jafar", "Jafar", "1234", "jafarabad", "Singer", "")));

        EventManager eventManager = new EventManager();
        eventManager.addEventHandler(new LoginEventHandler(mockDao));

        Server testServer = new Server(mockServerSocket, eventManager);
        testServer.start();
        Thread.sleep(50);
        testServer.stopServer();
        testServer.join();

        Optional<Response> receivedResponse = ResponseFactory.getResponse(
                new JSONObject(new JSONTokener(outputStream.toString()))
        );

        assertTrue(receivedResponse.isPresent());
        assertTrue(((LoginResponse) receivedResponse.get()).successful);
    }

    @Test
    public void testSocketAcceptError() throws Exception{
        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                new LoginRequest("jafar", "1234")
                        .toJSON().toString()
                        .getBytes(StandardCharsets.UTF_8)
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Socket mockSocket = mock(Socket.class);
        when(mockSocket.getInputStream())
                .thenReturn(inputStream);
        when(mockSocket.getOutputStream())
                .thenReturn(outputStream);

        ServerSocket mockServerSocket = mock(ServerSocket.class);
        when(mockServerSocket.accept())
                .thenThrow(IOException.class)
                .thenReturn(mockSocket);

        UserDao mockDao = mock(UserDao.class);
        when(mockDao.get("jafar"))
                .thenReturn(Optional.of(new User("jafar", "Jafar", "1234", "jafarabad", "Singer", "")));

        EventManager eventManager = new EventManager();
        eventManager.addEventHandler(new LoginEventHandler(mockDao));

        Server testServer = new Server(mockServerSocket, eventManager);
        testServer.start();
        Thread.sleep(50);
        testServer.stopServer();
        testServer.join();

        Optional<Response> receivedResponse = ResponseFactory.getResponse(
                new JSONObject(new JSONTokener(outputStream.toString()))
        );

        assertTrue(receivedResponse.isPresent());
        assertTrue(((LoginResponse) receivedResponse.get()).successful);
    }

}