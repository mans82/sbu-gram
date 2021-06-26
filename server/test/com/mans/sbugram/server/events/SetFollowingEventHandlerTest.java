package com.mans.sbugram.server.events;

import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.requests.SetFollowingRequest;
import com.mans.sbugram.models.responses.SetFollowingResponse;
import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SetFollowingEventHandlerTest {

    UserDao mockUserDao;
    SetFollowingEventHandler handler;

    User testUserJafar = new User("jafar", "Jafar", "1234", "", "", "", Collections.emptySet());
    User testUserAsghar = new User("asghar", "Asghar", "5678", "", "", "", Collections.singleton("jafar"));

    @Before
    public void setUp() {
        mockUserDao = mock(UserDao.class);
        handler = new SetFollowingEventHandler(mockUserDao);
    }

    @Test
    public void testRequestType() {
        assertEquals(RequestType.SET_FOLLOWING, handler.getRequestType());
    }

    @Test
    public void testProperRequestFollow() throws Exception {
        when(mockUserDao.get(testUserJafar.username))
                .thenReturn(Optional.of(testUserJafar));

        when(mockUserDao.get(testUserAsghar.username))
                .thenReturn(Optional.of(testUserAsghar));

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        doNothing().when(mockUserDao).update(eq(testUserJafar.username), userArgumentCaptor.capture());

        SetFollowingRequest request = new SetFollowingRequest(
                testUserJafar.username,
                testUserJafar.password,
                testUserAsghar.username,
                true
        );

        SetFollowingResponse response = (SetFollowingResponse) handler.handleEvent(request);

        assertTrue(response.successful);
        assertTrue(response.following);
        assertEquals(testUserJafar.username, userArgumentCaptor.getValue().username);
        assertEquals(1, userArgumentCaptor.getValue().followingUsersUsernames.size());
    }

    @Test
    public void testProperRequestUnfollow() throws Exception {
        when(mockUserDao.get(testUserJafar.username))
                .thenReturn(Optional.of(testUserJafar));

        when(mockUserDao.get(testUserAsghar.username))
                .thenReturn(Optional.of(testUserAsghar));

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        doNothing().when(mockUserDao).update(eq(testUserAsghar.username), userArgumentCaptor.capture());

        SetFollowingRequest request = new SetFollowingRequest(
                testUserAsghar.username,
                testUserAsghar.password,
                testUserJafar.username,
                false
        );

        SetFollowingResponse response = (SetFollowingResponse) handler.handleEvent(request);

        assertTrue(response.successful);
        assertFalse(response.following);
        assertTrue(userArgumentCaptor.getValue().followingUsersUsernames.isEmpty());
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

    @Test
    public void testPersistentOperationException() throws Exception {
        when(mockUserDao.get(testUserJafar.username))
                .thenThrow(PersistentOperationException.class);

        SetFollowingRequest request = new SetFollowingRequest(
                testUserJafar.username,
                testUserJafar.password,
                testUserJafar.username,
                false
        );

        SetFollowingResponse response = (SetFollowingResponse) handler.handleEvent(request);

        assertFalse(response.successful);
    }

    @Test
    public void testWrongCredentials() throws Exception{
        when(mockUserDao.get(testUserJafar.username))
                .thenReturn(Optional.of(testUserJafar));

        when(mockUserDao.get(testUserAsghar.username))
                .thenReturn(Optional.of(testUserAsghar));

        SetFollowingRequest request = new SetFollowingRequest(
                testUserJafar.username,
                "wrong_password",
                testUserAsghar.username,
                true
        );

        SetFollowingResponse response = (SetFollowingResponse) handler.handleEvent(request);

        assertFalse(response.successful);
    }

    @Test
    public void testFollowOneSelf() throws Exception {
        when(mockUserDao.get(testUserJafar.username))
                .thenReturn(Optional.of(testUserJafar));

        when(mockUserDao.get(testUserAsghar.username))
                .thenReturn(Optional.of(testUserAsghar));

        SetFollowingRequest request = new SetFollowingRequest(
                testUserJafar.username,
                testUserJafar.password,
                testUserJafar.username,
                false
        );

        SetFollowingResponse response = (SetFollowingResponse) handler.handleEvent(request);

        assertFalse(response.successful);
    }

    @Test
    public void testNonExistentTargetUser() throws Exception {
        when(mockUserDao.get(testUserJafar.username))
                .thenReturn(Optional.of(testUserJafar));

        when(mockUserDao.get(testUserAsghar.username))
                .thenReturn(Optional.empty());

        SetFollowingRequest request = new SetFollowingRequest(
                testUserJafar.username,
                testUserJafar.password,
                testUserAsghar.username,
                false
        );

        SetFollowingResponse response = (SetFollowingResponse) handler.handleEvent(request);

        assertFalse(response.successful);
    }

}