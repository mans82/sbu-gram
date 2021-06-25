package com.mans.sbugram.server.events;

import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.requests.SendPostRequest;
import com.mans.sbugram.models.responses.Response;
import com.mans.sbugram.models.responses.SendPostResponse;
import com.mans.sbugram.server.dao.impl.PostDao;
import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;

import java.util.Collections;
import java.util.Optional;

public class SendPostEventHandler implements EventHandler {

    public final UserDao userDao;
    public final PostDao postDao;

    public SendPostEventHandler(UserDao userDao, PostDao postDao) {
        this.userDao = userDao;
        this.postDao = postDao;
    }

    @Override
    public Response handleEvent(Request request) throws RequestTypeMismatchException {
        if (!(request instanceof SendPostRequest)) {
            throw new RequestTypeMismatchException(this.getRequestType().name());
        }

        SendPostRequest sendPostRequest = (SendPostRequest) request;

        Optional<User> queriedUserOptional;
        try {
            queriedUserOptional = userDao.get(sendPostRequest.username);
        } catch (PersistentOperationException e) {
            return new SendPostResponse(false, "Server error");
        }
        if (!queriedUserOptional.isPresent() || !queriedUserOptional.get().password.equals(sendPostRequest.password)) {
            return new SendPostResponse(false, "Wrong credentials");
        }

        User queriedUser = queriedUserOptional.get();

        Post sentPost = new Post(
                0,
                sendPostRequest.post.postedTime,
                sendPostRequest.post.title,
                sendPostRequest.post.content,
                sendPostRequest.post.photoFilename,
                queriedUser.username,
                Collections.emptySet(),
                Collections.emptySet()
        );

        try {
            postDao.save(sentPost);
        } catch (PersistentOperationException e) {
            return new SendPostResponse(false, "Server error");
        }

        return new SendPostResponse(true, "");
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SEND_POST;
    }
}
