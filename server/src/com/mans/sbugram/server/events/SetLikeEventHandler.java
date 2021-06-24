package com.mans.sbugram.server.events;

import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.requests.SetLikeRequest;
import com.mans.sbugram.models.responses.Response;
import com.mans.sbugram.models.responses.SetLikeResponse;
import com.mans.sbugram.server.dao.impl.PostDao;
import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentDataDoesNotExistException;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SetLikeEventHandler implements EventHandler {

    private final UserDao userDao;
    private final PostDao postDao;

    public SetLikeEventHandler(UserDao userDao, PostDao postDao) {
        this.userDao = userDao;
        this.postDao = postDao;
    }

    @Override
    public Response handleEvent(Request request) throws RequestTypeMismatchException {
        if (!(request instanceof SetLikeRequest)) {
            throw new RequestTypeMismatchException(this.getRequestType().name());
        }

        SetLikeRequest setLikeRequest = (SetLikeRequest) request;

        Optional<User> queriedUserOptional;
        try {
            queriedUserOptional = userDao.get(setLikeRequest.username);
        } catch (PersistentOperationException e) {
            return new SetLikeResponse(false, "Server error", false);
        }
        if (!queriedUserOptional.isPresent() || !queriedUserOptional.get().password.equals(setLikeRequest.password)) {
            return new SetLikeResponse(false, "Wrong credentials", false);
        }
        User queriedUser = queriedUserOptional.get();

        Optional<Post> queriedPostOptional;
        try {
            queriedPostOptional = postDao.get(setLikeRequest.postId);
        } catch (PersistentOperationException e) {
            return new SetLikeResponse(false, "Server error", false);
        }
        if (!queriedPostOptional.isPresent()) {
            return new SetLikeResponse(false, "Post not found", false);
        }
        Post queriedPost = queriedPostOptional.get();

        Set<String> newLikedUsersUsernames = new HashSet<>(queriedPost.likedUsersUsernames);
        if (setLikeRequest.liked) {
            newLikedUsersUsernames.add(setLikeRequest.username);
        } else {
            newLikedUsersUsernames.remove(setLikeRequest.username);
        }

        Post newPost = new Post(
                queriedPost.id,
                queriedPost.postedTime,
                queriedPost.title,
                queriedPost.content,
                queriedPost.photoFilename,
                queriedPost.posterUsername,
                queriedPost.comments,
                queriedPost.isRepost,
                queriedPost.repostedPostId,
                newLikedUsersUsernames
        );

        try {
            postDao.update(queriedPost.id, newPost);
        } catch (PersistentOperationException e) {
            return new SetLikeResponse(false, "Server error", false);
        } catch (PersistentDataDoesNotExistException e) {
            return new SetLikeResponse(false, "Post not found", false);
        }

        return new SetLikeResponse(true, "", newLikedUsersUsernames.contains(queriedUser.username));
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SET_LIKE;
    }
}
