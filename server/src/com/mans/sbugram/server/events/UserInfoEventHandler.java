package com.mans.sbugram.server.events;

import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.requests.UserInfoRequest;
import com.mans.sbugram.models.responses.Response;
import com.mans.sbugram.models.responses.UserInfoResponse;
import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;

import java.util.Optional;
import java.util.Set;

public class UserInfoEventHandler implements EventHandler {

    private final UserDao dao;

    public UserInfoEventHandler(UserDao dao) {
        this.dao = dao;
    }

    @Override
    public Response handleEvent(Request request) throws RequestTypeMismatchException {
        if (!(request instanceof UserInfoRequest)) {
            throw new RequestTypeMismatchException(getRequestType().name());
        }

        UserInfoRequest userInfoRequest = (UserInfoRequest) request;

        Optional<User> queriedUserOptional;
        try {
            queriedUserOptional = dao.get(userInfoRequest.username);
        } catch (PersistentOperationException e) {
            return new UserInfoResponse(false, "Server error", null);
        }

        if (queriedUserOptional.isPresent()) {
            User queriedUser = queriedUserOptional.get();
            String name, username, password, city, bio, profilePhotoFilename;
            Set<String> followingUsersUsernames;

            name = queriedUser.name;
            username = queriedUser.username;
            password = "";
            city = queriedUser.city;
            bio = queriedUser.bio;
            profilePhotoFilename = queriedUser.profilePhotoFilename;
            followingUsersUsernames = queriedUser.followingUsersUsernames;

            return new UserInfoResponse(
                    true,
                    "",
                    new User(username, name, password, city, bio, profilePhotoFilename, followingUsersUsernames)
            );
        } else {
            return new UserInfoResponse(false, "User not found", null);
        }
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.USER_INFO;
    }
}
