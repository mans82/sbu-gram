package com.mans.sbugram.server.events;

import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.requests.SetFollowingRequest;
import com.mans.sbugram.models.responses.Response;
import com.mans.sbugram.models.responses.SetFollowingResponse;
import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentDataDoesNotExistException;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SetFollowingEventHandler implements EventHandler{

    private final UserDao userDao;

    public SetFollowingEventHandler(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Response handleEvent(Request request) throws RequestTypeMismatchException {
        if (!(request instanceof SetFollowingRequest)) {
            throw new RequestTypeMismatchException(this.getRequestType().name());
        }

        SetFollowingRequest setFollowingRequest = (SetFollowingRequest) request;

        Optional<User> queriedUserOptional;
        try {
            queriedUserOptional = userDao.get(setFollowingRequest.username);
        } catch (PersistentOperationException e) {
            return new SetFollowingResponse(false, "Server error", false);
        }
        if (!queriedUserOptional.isPresent() || !queriedUserOptional.get().password.equals(setFollowingRequest.password)) {
            return new SetFollowingResponse(false, "Wrong credentials", false);
        }

        User queriedUser = queriedUserOptional.get();

        Optional<User> queriedFollowedUserOptional;
        try {
            queriedFollowedUserOptional = userDao.get(setFollowingRequest.targetUserUsername);
        } catch (PersistentOperationException e) {
            return new SetFollowingResponse(false, "Server error", false);
        }
        if (!queriedFollowedUserOptional.isPresent()) {
            return new SetFollowingResponse(false, "Target user not found", false);
        }

        User queriedFollowedUser = queriedFollowedUserOptional.get();

        if (queriedUser.username.equals(queriedFollowedUser.username)) {
            return new SetFollowingResponse(false, "Cannot follow oneself", false);
        }
        Set<String> newFollowingUsersUsernames = new HashSet<>(queriedUser.followingUsersUsernames);

        if (setFollowingRequest.following) {
            newFollowingUsersUsernames.add(queriedFollowedUser.username);
        } else {
            newFollowingUsersUsernames.remove(queriedFollowedUser.username);
        }

        User newUser = new User(
                queriedUser.username,
                queriedUser.name,
                queriedUser.password,
                queriedUser.city,
                queriedUser.bio,
                queriedUser.profilePhotoFilename,
                newFollowingUsersUsernames
        );

        try {
            userDao.update(queriedUser.username, newUser);
        } catch (PersistentOperationException e) {
            return new SetFollowingResponse(false, "Server error", false);
        } catch (PersistentDataDoesNotExistException e) {
            return new SetFollowingResponse(false, "User info not found", false);
        }

        return new SetFollowingResponse(true, "", newFollowingUsersUsernames.contains(queriedFollowedUser.username));
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SET_FOLLOWING;
    }
}
