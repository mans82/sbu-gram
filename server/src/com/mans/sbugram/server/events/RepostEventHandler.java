package com.mans.sbugram.server.events;

import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.RepostRequest;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.responses.RepostResponse;
import com.mans.sbugram.models.responses.Response;
import com.mans.sbugram.server.dao.impl.PostDao;
import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

public class RepostEventHandler implements EventHandler {

    private final UserDao userDao;
    private final PostDao postDao;

    public RepostEventHandler(UserDao userDao, PostDao postDao) {
        this.userDao = userDao;
        this.postDao = postDao;
    }

    @Override
    public Response handleEvent(Request request) throws RequestTypeMismatchException {
        if(!(request instanceof RepostRequest)) {
            throw new RequestTypeMismatchException(this.getRequestType().name());
        }

        RepostRequest repostRequest = (RepostRequest) request;

        Optional<User> queriedUserOptional;
        try {
            queriedUserOptional = userDao.get(repostRequest.username);
        } catch (PersistentOperationException e) {
            return new RepostResponse(false, "Server error");
        }
        if (!queriedUserOptional.isPresent() || !queriedUserOptional.get().password.equals(repostRequest.password)) {
            return new RepostResponse(false, "Wrong credentials");
        }
        User queriedUser = queriedUserOptional.get();

        Optional<Post> queriedPostOptional;
        try {
            queriedPostOptional = postDao.get(repostRequest.repostedPostId);
        } catch (PersistentOperationException e) {
            return new RepostResponse(false, "Server error");
        }
        if (!queriedPostOptional.isPresent()) {
            return new RepostResponse(false, "Post not found");
        }
        Post queriedPost = queriedPostOptional.get();

        Post newPost = new Post(
                0,
                Instant.now().getEpochSecond(),
                queriedPost.title,
                queriedPost.content,
                queriedPost.photoFilename,
                queriedUser.username,
                Collections.emptySet(),
                true,
                queriedPost.posterUsername,
                Collections.emptySet()
        );

        try {
            postDao.save(newPost);
        } catch (PersistentOperationException e) {
            return new RepostResponse(false, "Server error");
        }

        System.out.printf(
                "%s reposted\nMessage: %s %s\nTime: %s\n\n",
                newPost.posterUsername,
                newPost.originalPosterUsername,
                newPost.title,
                this.formatDate(Instant.now())
        );
        return new RepostResponse(true, "");
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.REPOST;
    }
}
