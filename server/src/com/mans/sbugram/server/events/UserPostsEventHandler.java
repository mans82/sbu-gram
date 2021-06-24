package com.mans.sbugram.server.events;

import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.requests.UserPostsRequest;
import com.mans.sbugram.models.responses.Response;
import com.mans.sbugram.models.responses.UserPostsResponse;
import com.mans.sbugram.server.dao.impl.PostDao;
import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserPostsEventHandler implements EventHandler {

    private final UserDao userDao;
    private final PostDao postDao;

    public UserPostsEventHandler(UserDao userDao, PostDao postDao) {
        this.userDao = userDao;
        this.postDao = postDao;
    }

    @Override
    public Response handleEvent(Request request) throws RequestTypeMismatchException {
        if (!(request instanceof UserPostsRequest)) {
            throw new RequestTypeMismatchException(this.getRequestType().name());
        }

        UserPostsRequest userPostsRequest = (UserPostsRequest) request;

        Optional<User> queriedUserOptional;
        try {
            queriedUserOptional = userDao.get(userPostsRequest.username);
        } catch (PersistentOperationException e) {
            return new UserPostsResponse(false, "Server error", Collections.emptyList());
        }
        if (!queriedUserOptional.isPresent()) {
            return new UserPostsResponse(false, "Invalid username", Collections.emptyList());
        }

        User queriedUser = queriedUserOptional.get();

        List<Post> queriedPosts = postDao.getPosts(post -> post.posterUsername.equals(queriedUser.username), 100);

        return new UserPostsResponse(true, "", queriedPosts);
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.USER_POSTS;
    }
}
