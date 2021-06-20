package com.mans.sbugram.server.events;

import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.requests.UserTimelineRequest;
import com.mans.sbugram.models.responses.UserTimelineResponse;
import com.mans.sbugram.server.dao.impl.PostDao;
import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.util.collections.Sets;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserTimelineEventHandlerTest {

    PostDao mockPostDao;
    UserDao mockUserDao;
    UserTimelineEventHandler handler;

    @Before
    public void setUp() {
        mockPostDao = mock(PostDao.class);
        mockUserDao = mock(UserDao.class);
        handler = new UserTimelineEventHandler(mockUserDao, mockPostDao);
    }

    @Test
    public void testRequestType() {
        assertEquals(RequestType.USER_TIMELINE, handler.getRequestType());
    }

    @Test
    public void testUserTimelineCorrectPredicate() throws Exception {
        Post[] testPosts = {
                new Post(1, 13, "title1", "content1", "", "jafar"),
                new Post(2, 12, "title2", "content2", "", "asgar"),
                new Post(3, 15, "title3", "content3", "", "maar_haye_asgar")
        };

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Predicate<Post>> predicateArgumentCaptor = ArgumentCaptor.forClass(Predicate.class);

        when(mockUserDao.get("jafar"))
                .thenReturn(Optional.of(
                        new User("jafar", "Jafar", "1234", "", "", "", Sets.newSet("asgar"))
                ));

        UserTimelineResponse receivedResponse = (UserTimelineResponse) handler.handleEvent(
                new UserTimelineRequest("jafar", "1234", 12, 10)
        );

        verify(mockPostDao).getPosts(predicateArgumentCaptor.capture(), eq(10));

        List<Post> filteredPosts = Arrays.stream(testPosts).filter(predicateArgumentCaptor.getValue()).collect(Collectors.toList());

        assertEquals(2, filteredPosts.size());
        assertTrue(filteredPosts.stream().anyMatch(post -> post.id == 1));
        assertTrue(filteredPosts.stream().anyMatch(post -> post.id == 2));
        assertTrue(filteredPosts.stream().noneMatch(post -> post.id == 3));

        assertTrue(receivedResponse.successful);
    }

    @Test
    public void testUserTimelineWrongUsername() throws Exception {
        when(mockUserDao.get("jafar"))
                .thenReturn(Optional.empty());

        UserTimelineResponse receivedResponse = (UserTimelineResponse) handler.handleEvent(
                new UserTimelineRequest("jafar", "password", 12, 10)
        );

        assertFalse(receivedResponse.successful);
    }

    @Test
    public void testUserTimelineWrongPassword() throws Exception {
        when(mockUserDao.get("jafar"))
                .thenReturn(Optional.of(
                        new User("jafar", "Jafar", "1234", "", "", "", Sets.newSet("asgar"))
                ));

        UserTimelineResponse receivedResponse = (UserTimelineResponse) handler.handleEvent(
                new UserTimelineRequest("jafar", "wrong_password", 12, 10)
        );

        assertFalse(receivedResponse.successful);
    }

    @Test
    public void testUserTimelinePersistentException() throws Exception{
        when(mockUserDao.get(anyString()))
                .thenThrow(PersistentOperationException.class);

        UserTimelineResponse receivedResponse = (UserTimelineResponse) handler.handleEvent(
                new UserTimelineRequest("jafar", "password", 12, 10)
        );

        assertFalse(receivedResponse.successful);
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
}