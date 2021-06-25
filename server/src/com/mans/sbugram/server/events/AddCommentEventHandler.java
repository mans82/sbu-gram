package com.mans.sbugram.server.events;

import com.mans.sbugram.models.Comment;
import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.AddCommentRequest;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.responses.AddCommentResponse;
import com.mans.sbugram.models.responses.Response;
import com.mans.sbugram.server.dao.impl.PostDao;
import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentDataDoesNotExistException;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;

import java.util.HashSet;
import java.util.Optional;

public class AddCommentEventHandler implements EventHandler {

    private final UserDao userDao;
    private final PostDao postDao;

    public AddCommentEventHandler(UserDao userDao, PostDao postDao) {
        this.userDao = userDao;
        this.postDao = postDao;
    }

    @Override
    public Response handleEvent(Request request) throws RequestTypeMismatchException {
        if (!(request instanceof AddCommentRequest)) {
            throw new RequestTypeMismatchException(this.getRequestType().name());
        }
        AddCommentRequest addCommentRequest = (AddCommentRequest) request;

        if (addCommentRequest.text.isEmpty()) {
            return new AddCommentResponse(false, "Empty comment is not allowed");
        }

        Optional<User> queriedUser;
        try {
            queriedUser = userDao.get(addCommentRequest.username);
        } catch (PersistentOperationException e) {
            return new AddCommentResponse(false, "Server error");
        }
        if (!queriedUser.isPresent() || !queriedUser.get().password.equals(addCommentRequest.password)) {
            return new AddCommentResponse(false, "Wrong credentials");
        }

        Optional<Post> queriedPost;
        try {
            queriedPost = postDao.get(addCommentRequest.postId);
        } catch (PersistentOperationException e) {
            return new AddCommentResponse(false, "Server error");
        }
        if (!queriedPost.isPresent() || queriedPost.get().id != addCommentRequest.postId) {
            return new AddCommentResponse(false, "Wrong post id");
        }

        HashSet<Comment> newComments = new HashSet<>(queriedPost.get().comments);
        newComments.add(new Comment(addCommentRequest.username, addCommentRequest.text));

        Post newPost = new Post(
                queriedPost.get().id,
                queriedPost.get().postedTime,
                queriedPost.get().title,
                queriedPost.get().content,
                queriedPost.get().photoFilename,
                queriedPost.get().posterUsername,
                newComments,
                queriedPost.get().isRepost,
                queriedPost.get().originalPosterUsername,
                queriedPost.get().likedUsersUsernames);

        try {
            postDao.update(queriedPost.get().id, newPost);
        } catch (PersistentOperationException e) {
            return new AddCommentResponse(false, "Server error");
        } catch (PersistentDataDoesNotExistException e) {
            return new AddCommentResponse(false, "Post does not exist");
        }

        return new AddCommentResponse(true, "");
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.ADD_COMMENT;
    }
}
