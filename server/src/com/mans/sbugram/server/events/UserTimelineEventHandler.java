package com.mans.sbugram.server.events;

import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.requests.UserTimelineRequest;
import com.mans.sbugram.models.responses.Response;
import com.mans.sbugram.models.responses.UserTimelineResponse;
import com.mans.sbugram.server.dao.impl.PostDao;
import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserTimelineEventHandler implements EventHandler {

    private final UserDao userDao;
    private final PostDao postDao;

    public UserTimelineEventHandler(UserDao userDao, PostDao postDao) {
        this.userDao = userDao;
        this.postDao = postDao;
    }

    @Override
    public Response handleEvent(Request request) throws RequestTypeMismatchException {
        if (!(request instanceof UserTimelineRequest)) {
            throw new RequestTypeMismatchException(this.getRequestType().name());
        }
        UserTimelineRequest userTimelineRequest = (UserTimelineRequest) request;

        Optional<User> requestSenderUserOptional;
        try {
            requestSenderUserOptional = userDao.get(userTimelineRequest.username);
        } catch (PersistentOperationException e) {
            return new UserTimelineResponse(false, "Server error", Collections.emptyList());
        }

        if (!requestSenderUserOptional.isPresent() || !requestSenderUserOptional.get().password.equals(userTimelineRequest.password)) {
            return new UserTimelineResponse(false, "Wrong credentials", Collections.emptyList());
        }

        User requestSenderUser = requestSenderUserOptional.get();

        List<Post> timelinePosts = postDao.getPosts(
                post -> post.posterUsername.equals(requestSenderUser.username) || requestSenderUser.followingUsersUsernames.contains(post.posterUsername),
                userTimelineRequest.count
        );

        System.out.printf(
                "%s requested timeline\nTime: %s\n\n",
                userTimelineRequest.username,
                this.formatDate(Instant.now())
        );
        return new UserTimelineResponse(true, "", timelinePosts);
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.USER_TIMELINE;
    }
}
