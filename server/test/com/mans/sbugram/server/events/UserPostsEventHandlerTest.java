package com.mans.sbugram.server.events;

import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.requests.UserPostsRequest;
import com.mans.sbugram.models.responses.UserPostsResponse;
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
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserPostsEventHandlerTest {

    UserDao mockUserDao;
    PostDao mockPostDao;
    UserPostsEventHandler handler;

    User testUser = new User("jafar", "Jafar", "1234", "", "", "", Collections.emptySet());
    Post testPost = new Post(1, 111, "title", "content", "", "jafar", Collections.emptySet(), Collections.emptySet());

    @Before
    public void setUp() {
        mockUserDao = mock(UserDao.class);
        mockPostDao = mock(PostDao.class);
        handler = new UserPostsEventHandler(mockUserDao, mockPostDao);
    }

    @Test
    public void testRequestType() {
        assertEquals(RequestType.USER_POSTS, handler.getRequestType());
    }

    @Test
    public void testProperRequest() throws Exception {
        when(mockUserDao.get(testUser.username))
                .thenReturn(Optional.of(testUser));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Predicate<Post>> predicateArgumentCaptor = ArgumentCaptor.forClass(Predicate.class);

        when(mockPostDao.getPosts(predicateArgumentCaptor.capture(), anyInt()))
                .thenReturn(Collections.emptyList());

        UserPostsRequest request = new UserPostsRequest(testUser.username);
        UserPostsResponse response = (UserPostsResponse) handler.handleEvent(request);

        assertTrue(response.successful);
        assertTrue(predicateArgumentCaptor.getValue().test(testPost));
    }

    @Test(expected = RequestTypeMismatchException.class)
    public void testRequestMismatch() throws Exception{
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

        when(mockPostDao.getPosts(any(), anyInt()))
                .thenReturn(Collections.emptyList());

        UserPostsRequest request = new UserPostsRequest(testUser.username);
        UserPostsResponse response = (UserPostsResponse) handler.handleEvent(request);

        assertFalse(response.successful);
    }

    @Test
    public void testNonExistentUser() throws Exception {
        when(mockUserDao.get(testUser.username))
                .thenReturn(Optional.empty());

        when(mockPostDao.getPosts(any(), anyInt()))
                .thenReturn(Collections.emptyList());

        UserPostsRequest request = new UserPostsRequest(testUser.username);
        UserPostsResponse response = (UserPostsResponse) handler.handleEvent(request);

        assertFalse(response.successful);
    }

}