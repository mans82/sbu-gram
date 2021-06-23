package com.mans.sbugram.server.events;

import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.AddCommentRequest;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.responses.AddCommentResponse;
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

public class AddCommentEventHandlerTest {

    UserDao mockUserDao;
    PostDao mockPostDao;
    AddCommentEventHandler handler;

    User testUser = new User("jafar", "Jafar", "1234", "", "", "", Collections.emptySet());
    Post testPost = new Post(0, 0, "title", "content", "", "jafar", Collections.emptySet());

    @Before
    public void setUp() {
        mockUserDao = mock(UserDao.class);
        mockPostDao = mock(PostDao.class);

        handler = new AddCommentEventHandler(mockUserDao, mockPostDao);
    }

    @Test
    public void testRequestType() {
        assertEquals(RequestType.ADD_COMMENT, handler.getRequestType());
    }

    @Test
    public void testProperComment() throws Exception {
        when(mockUserDao.get("jafar"))
                .thenReturn(Optional.of(testUser));

        when(mockPostDao.get(0))
                .thenReturn(Optional.of(testPost));

        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);

        AddCommentResponse response = (AddCommentResponse) handler.handleEvent(
                new AddCommentRequest(testUser.username, testUser.password, testPost.id, "Hello!")
        );

        assertTrue(response.successful);

        verify(mockPostDao).update(eq(0), postArgumentCaptor.capture());
        assertEquals(1, postArgumentCaptor.getValue().comments.size());
    }

    @Test
    public void testEmptyComment() throws Exception {
        when(mockUserDao.get("jafar"))
                .thenReturn(Optional.of(testUser));

        when(mockPostDao.get(0))
                .thenReturn(Optional.of(testPost));

        AddCommentResponse response = (AddCommentResponse) handler.handleEvent(
                new AddCommentRequest(testUser.username, testUser.password, testPost.id, "")
        );

        assertFalse(response.successful);
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
    public void testPersistentOperationExceptionUserDao() throws Exception{
        when(mockUserDao.get(testUser.username))
                .thenThrow(PersistentOperationException.class);

        when(mockPostDao.get(0))
                .thenReturn(Optional.of(testPost));

        AddCommentResponse response = (AddCommentResponse) handler.handleEvent(new AddCommentRequest(
                testUser.username,
                testUser.password,
                testPost.id,
                "Some comment"
        ));

        assertFalse(response.successful);
    }

    @Test
    public void testPersistentOperationExceptionPostDao() throws Exception {
        when(mockPostDao.get(testPost.id))
                .thenThrow(PersistentOperationException.class);

        when(mockUserDao.get(testUser.username))
                .thenReturn(Optional.of(testUser));

        AddCommentResponse response = (AddCommentResponse) handler.handleEvent(new AddCommentRequest(
                testUser.username,
                testUser.password,
                testPost.id,
                "Some comment"
        ));

        assertFalse(response.successful);
    }

    @Test
    public void testPersistentOperationExceptionPostDaoUpdate() throws Exception {
        when(mockPostDao.get(0))
                .thenReturn(Optional.of(testPost));

        doThrow(PersistentOperationException.class)
                .when(mockPostDao).update(anyInt(), any(Post.class));

        when(mockUserDao.get(testUser.username))
                .thenReturn(Optional.of(testUser));

        AddCommentResponse response = (AddCommentResponse) handler.handleEvent(new AddCommentRequest(
                testUser.username,
                testUser.password,
                testPost.id,
                "Some comment"
        ));

        assertFalse(response.successful);
    }

    @Test
    public void testWrongCredentials() throws Exception {
        when(mockUserDao.get("jafar"))
                .thenReturn(Optional.of(testUser));

        when(mockPostDao.get(0))
                .thenReturn(Optional.of(testPost));

        AddCommentResponse response = (AddCommentResponse) handler.handleEvent(new AddCommentRequest(
                testUser.username,
                "wrong_password",
                testPost.id,
                "Some comment"
        ));

        assertFalse(response.successful);
    }

    @Test
    public void testWrongPostId() throws Exception {
        when(mockUserDao.get("jafar"))
                .thenReturn(Optional.of(testUser));

        when(mockPostDao.get(0))
                .thenReturn(Optional.empty());

        AddCommentResponse response = (AddCommentResponse) handler.handleEvent(new AddCommentRequest(
                testUser.username,
                testUser.password,
                0,
                "Some comment"
        ));

        assertFalse(response.successful);
    }

}