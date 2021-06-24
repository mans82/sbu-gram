package com.mans.sbugram.server.events;

import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.requests.SetLikeRequest;
import com.mans.sbugram.models.responses.SetLikeResponse;
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

public class SetLikeEventHandlerTest {

    UserDao mockUserDao;
    PostDao mockPostDao;
    SetLikeEventHandler handler;

    Post testPost = new Post(
            1,
            111,
            "title",
            "content",
            "",
            "jafar",
            Collections.emptySet(),
            Collections.emptySet()
    );

    User testUser = new User(
            "jafar",
            "Jafar",
            "1234",
            "jafar abad",
            "",
            "",
            Collections.emptySet()
    );

    @Before
    public void setUp() {
        mockUserDao = mock(UserDao.class);
        mockPostDao = mock(PostDao.class);
        handler = new SetLikeEventHandler(mockUserDao, mockPostDao);
    }

    @Test
    public void testRequestType() {
        assertEquals(RequestType.SET_LIKE, handler.getRequestType());
    }

    @Test
    public void testProperRequestLike() throws Exception {
        when(mockUserDao.get(testUser.username))
                .thenReturn(Optional.of(testUser));

        when(mockPostDao.get(testPost.id))
                .thenReturn(Optional.of(testPost));

        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);

        doNothing().when(mockPostDao).update(eq(testPost.id), postArgumentCaptor.capture());

        SetLikeRequest request = new SetLikeRequest(
                testUser.username,
                testUser.password,
                testPost.id,
                true
        );

        SetLikeResponse response = (SetLikeResponse) handler.handleEvent(request);

        assertTrue(response.successful);
        assertTrue(response.liked);
        assertEquals(1, postArgumentCaptor.getValue().likedUsersUsernames.size());
        assertTrue(postArgumentCaptor.getValue().likedUsersUsernames.contains(testUser.username));
    }

    @Test
    public void testProperRequestDislike() throws Exception {
        when(mockUserDao.get(testUser.username))
                .thenReturn(Optional.of(testUser));

        when(mockPostDao.get(testPost.id))
                .thenReturn(Optional.of(new Post(
                        testPost.id,
                        testPost.postedTime,
                        testPost.title,
                        testPost.content,
                        testPost.photoFilename,
                        testPost.posterUsername,
                        testPost.comments,
                        testPost.isRepost,
                        testPost.repostedPostId,
                        Collections.singleton(testUser.username)
                )));

        SetLikeRequest request = new SetLikeRequest(
                testUser.username,
                testUser.password,
                testPost.id,
                false
        );

        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);

        doNothing().when(mockPostDao).update(eq(testPost.id), postArgumentCaptor.capture());

        SetLikeResponse response = (SetLikeResponse) handler.handleEvent(request);

        assertTrue(response.successful);
        assertFalse(response.liked);
        assertTrue(postArgumentCaptor.getValue().likedUsersUsernames.isEmpty());
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
        when(mockUserDao.get(testUser.username))
                .thenThrow(PersistentOperationException.class);

        when(mockPostDao.get(testPost.id))
                .thenReturn(Optional.of(testPost));

        SetLikeRequest request = new SetLikeRequest(
                testUser.username,
                testUser.password,
                testPost.id,
                true
        );

        SetLikeResponse response = (SetLikeResponse) handler.handleEvent(request);

        assertFalse(response.successful);
    }

    @Test
    public void testPostDaoPersistentOperationException() throws Exception {
        when(mockUserDao.get(testUser.username))
                .thenReturn(Optional.of(testUser));

        when(mockPostDao.get(testPost.id))
                .thenThrow(PersistentOperationException.class);

        SetLikeRequest request = new SetLikeRequest(
                testUser.username,
                testUser.password,
                testPost.id,
                true
        );

        SetLikeResponse response = (SetLikeResponse) handler.handleEvent(request);

        assertFalse(response.successful);
    }

    @Test
    public void testPostDaoUpdatePersistentOperationException() throws Exception {
        when(mockUserDao.get(testUser.username))
                .thenReturn(Optional.of(testUser));

        when(mockPostDao.get(testPost.id))
                .thenReturn(Optional.of(testPost));

        doThrow(PersistentOperationException.class)
                .when(mockPostDao).update(anyInt(), any(Post.class));

        SetLikeRequest request = new SetLikeRequest(
                testUser.username,
                testUser.password,
                testPost.id,
                true
        );

        SetLikeResponse response = (SetLikeResponse) handler.handleEvent(request);

        assertFalse(response.successful);
    }

    @Test
    public void testWrongCredentials() throws Exception {
        when(mockUserDao.get(testUser.username))
                .thenReturn(Optional.of(testUser));

        when(mockPostDao.get(testPost.id))
                .thenReturn(Optional.of(testPost));

        SetLikeRequest request = new SetLikeRequest(
                testUser.username,
                "wrongpassword",
                testPost.id,
                true
        );

        SetLikeResponse response = (SetLikeResponse) handler.handleEvent(request);

        assertFalse(response.successful);
    }

}