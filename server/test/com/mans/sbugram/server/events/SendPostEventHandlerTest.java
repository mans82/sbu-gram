package com.mans.sbugram.server.events;

import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.requests.SendPostRequest;
import com.mans.sbugram.models.responses.SendPostResponse;
import com.mans.sbugram.server.dao.impl.PostDao;
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

public class SendPostEventHandlerTest {

    UserDao mockUserDao;
    PostDao mockPostDao;
    SendPostEventHandler handler;

    Post testPost = new Post(0, 11, "title", "content", "", "", Collections.emptySet(), Collections.emptySet());
    User testUser = new User("jafar", "Jafar", "1234", "", "", "", Collections.emptySet());

    @Before
    public void setUp() {
        mockUserDao = mock(UserDao.class);
        mockPostDao = mock(PostDao.class);
        handler = new SendPostEventHandler(mockUserDao, mockPostDao);
    }

    @Test
    public void testRequestType() {
        assertEquals(RequestType.SEND_POST, handler.getRequestType());
    }

    @Test
    public void testProperRequest() throws Exception {
        when(mockUserDao.get(testUser.username))
                .thenReturn(Optional.of(testUser));

        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);
        doNothing().when(mockPostDao).save(postArgumentCaptor.capture());

        SendPostRequest request = new SendPostRequest(testUser.username, testUser.password, testPost);

        SendPostResponse response = (SendPostResponse) handler.handleEvent(request);

        assertTrue(response.successful);
        assertEquals(testUser.username, postArgumentCaptor.getValue().posterUsername);
    }

    @Test(expected = RequestTypeMismatchException.class)
    public void testRequestMismatch() throws Exception {
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
    public void testUserDaoPersistentOperationException() throws Exception {
        when(mockUserDao.get(anyString()))
                .thenThrow(PersistentOperationException.class);

        SendPostRequest request = new SendPostRequest(testUser.username, testUser.password, testPost);

        SendPostResponse response = (SendPostResponse) handler.handleEvent(request);

        assertFalse(response.successful);
    }

    @Test
    public void testWrongCredentials() throws Exception {
        when(mockUserDao.get(testUser.username))
                .thenReturn(Optional.of(testUser));

        SendPostRequest request = new SendPostRequest(
                testUser.username,
                "wrong_password",
                testPost
        );

        SendPostResponse response = (SendPostResponse) handler.handleEvent(request);

        assertFalse(response.successful);
    }

}