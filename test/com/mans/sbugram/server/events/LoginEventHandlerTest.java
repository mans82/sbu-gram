package com.mans.sbugram.server.events;

import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;
import com.mans.sbugram.server.models.User;
import com.mans.sbugram.server.models.requests.LoginRequest;
import com.mans.sbugram.server.models.requests.Request;
import com.mans.sbugram.server.models.requests.RequestType;
import com.mans.sbugram.server.models.responses.LoginResponse;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginEventHandlerTest {

    UserDao mockedDao;
    User testUser = new User("jafar", "Jafar Jafari", "1234", "jafarabad", "Singer");
    LoginEventHandler handler;

    @Before
    public void setup() throws PersistentOperationException {
        mockedDao = mock(UserDao.class);

        when(mockedDao.get("jafar"))
                .thenReturn(Optional.of(testUser));

        handler = new LoginEventHandler(mockedDao);
    }

    @Test
    public void testRequestType() {
        assertEquals(RequestType.LOGIN, handler.getRequestType());
    }

    @Test
    public void testHandleCorrectCredentials() throws RequestTypeMismatchException {
        assertTrue(
                ((LoginResponse)handler.handleEvent(
                        new LoginRequest(testUser.username, testUser.password))
                ).successful);
    }

    @Test
    public void testHandlerWrongPassword() throws RequestTypeMismatchException {
        assertFalse(((LoginResponse) handler.handleEvent(
                new LoginRequest(testUser.username, "wrong_password")
        )).successful);
    }

    @Test
    public void testHandlerWrongUsername() throws RequestTypeMismatchException {
        assertFalse(((LoginResponse) handler.handleEvent(
                new LoginRequest("wrong_username", "qwerty")
        )).successful);
    }

    @Test
    public void testPersistentDataException() throws PersistentOperationException, RequestTypeMismatchException {
        when(mockedDao.get("jafar"))
                .thenThrow(new PersistentOperationException(null));

        assertFalse(((LoginResponse) handler.handleEvent(
                new LoginRequest("jafar", "1234")
        )).successful);
    }

    @Test
    public void testMismatchedRequest() {
        assertThrows(RequestTypeMismatchException.class, () -> handler.handleEvent(new Request() {
            @Override
            public RequestType getRequestType() {
                return null;
            }

            @Override
            public JSONObject toJSON() {
                return null;
            }
        }));
    }
}
