package com.mans.sbugram.server.events;

import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.LoginRequest;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.responses.LoginResponse;
import com.mans.sbugram.models.responses.Response;

import java.util.Optional;

public class LoginEventHandler implements EventHandler {

    final private UserDao userDao;

    public LoginEventHandler(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Response handleEvent(Request request) throws RequestTypeMismatchException {
        if (!(request instanceof LoginRequest)) {
            throw new RequestTypeMismatchException(this.getRequestType().name());
        }


        LoginRequest loginRequest = (LoginRequest) request;
        Optional<User> queriedUser;
        try {
            queriedUser = this.userDao.get(loginRequest.username);
        } catch (PersistentOperationException e) {
            return new LoginResponse(false, "Server failure");
        }

        if (queriedUser.isPresent() && queriedUser.get().password.equals(loginRequest.password)) {
            return new LoginResponse(true, null);
        } else {
            return new LoginResponse(false, "Incorrect username or password");
        }

    }

    @Override
    public RequestType getRequestType() {
        return RequestType.LOGIN;
    }
}
