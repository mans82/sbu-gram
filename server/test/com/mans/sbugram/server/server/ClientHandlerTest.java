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
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ClientHandlerTest {

    private final User testUser = new User("jafar", "Jafar", "1234", "jafarabad", "Singer");
    private final UserDao mockDao = mock(UserDao.class);

    @Before
    public void setUp() throws Exception{
        when(mockDao.get("jafar"))
                .thenReturn(Optional.of(testUser));
    }

    @Test
    public void testCorrectCredentials() throws Exception {

        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                new LoginRequest("jafar", "1234")
                        .toJSON().toString()
                        .getBytes(StandardCharsets.UTF_8)
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        EventManager eventManager = new EventManager();
        eventManager.addEventHandler(new LoginEventHandler(mockDao));

        ClientHandler handler = new ClientHandler(inputStream, outputStream, eventManager);
        handler.run();

        Optional<Response> receivedResult = ResponseFactory.getResponse(new JSONObject(new JSONTokener(outputStream.toString())));

        assertTrue(receivedResult.isPresent());
        assertTrue(((LoginResponse)receivedResult.get()).successful);
    }

    @Test
    public void testBadRequestInvalidRequestType() throws Exception {
        JSONObject badRequestJson = new JSONObject()
                .put("request_type", "invalid")
                .put("data", testUser.toJSON());

        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                badRequestJson.toString()
                .getBytes(StandardCharsets.UTF_8)
        );

        ByteArrayOutputStream outputStream = mock(ByteArrayOutputStream.class);

        EventManager eventManager = new EventManager();
        eventManager.addEventHandler(new LoginEventHandler(mockDao));

        ClientHandler handler = new ClientHandler(inputStream, outputStream, eventManager);
        handler.run();

        verify(outputStream, times(1)).close();
    }

    @Test
    public void testBadRequestNoHandlerForRequest() throws Exception {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                new LoginRequest("jafar", "1234")
                .toJSON().toString()
                .getBytes(StandardCharsets.UTF_8)
        );

        ByteArrayOutputStream outputStream = mock(ByteArrayOutputStream.class);

        ClientHandler handler = new ClientHandler(inputStream, outputStream, new EventManager());
        handler.run();

        verify(outputStream, times(1)).close();
    }

}