package com.mans.sbugram.server.events;

import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.requests.UserInfoRequest;
import com.mans.sbugram.models.responses.UserInfoResponse;
import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserInfoEventHandlerTest {

    UserDao mockUserDao;
    UserInfoEventHandler handler;
    final User testUser = new User("jafar", "Jafar", "1234", "Jafar Abad", "", "", Collections.emptySet());

    @Before
    public void setUp() {
        mockUserDao = mock(UserDao.class);
        handler = new UserInfoEventHandler(mockUserDao);
    }

    @Test
    public void testRequestType() {
        assertEquals(RequestType.USER_INFO, handler.getRequestType());
    }

    @Test
    public void testProperUsername() throws Exception {
        when(mockUserDao.get("jafar"))
                .thenReturn(Optional.of(testUser));

        UserInfoResponse response = (UserInfoResponse) handler.handleEvent(
                new UserInfoRequest("jafar")
        );

        assertTrue(response.successful);
        assertNotNull(response.user);
        assertNotEquals(testUser.password, response.user.password);
    }

    @Test
    public void testNonExistentUsername() throws Exception {
        when(mockUserDao.get("asghar"))
                .thenReturn(Optional.empty());

        UserInfoResponse response = (UserInfoResponse) handler.handleEvent(
                new UserInfoRequest("asghar")
        );

        assertFalse(response.successful);
        assertNull(response.user);
    }

    @Test
    public void testPersistentOperationException() throws Exception {
        when(mockUserDao.get("jafar"))
                .thenThrow(PersistentOperationException.class);

        UserInfoResponse response = (UserInfoResponse) handler.handleEvent(
                new UserInfoRequest("jafar")
        );

        assertFalse(response.successful);
        assertNull(response.user);
    }

    @Test(expected = RequestTypeMismatchException.class)
    public void testRequestTypeMismatch() throws Exception {
        handler.handleEvent(new Request() {
            @Override
            public RequestType getRequestType() {
                return RequestType.LOGIN;
            }

            @Override
            public JSONObject toJSON() {
                return null;
            }
        });
    }
}