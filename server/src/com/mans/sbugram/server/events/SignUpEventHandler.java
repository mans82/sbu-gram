package com.mans.sbugram.server.events;

import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.requests.SignUpRequest;
import com.mans.sbugram.models.responses.Response;
import com.mans.sbugram.models.responses.SignUpResponse;

public class SignUpEventHandler implements EventHandler{

    private final UserDao dao;

    public SignUpEventHandler(UserDao dao) {
        this.dao = dao;
    }

    @Override
    public Response handleEvent(Request request) throws RequestTypeMismatchException {
        if (!(request instanceof SignUpRequest)) {
            throw new RequestTypeMismatchException(this.getRequestType().name());
        }

        SignUpRequest signUpRequest = (SignUpRequest) request;

        try {
            if (dao.get(signUpRequest.user.username).isPresent()) {
                return new SignUpResponse(false, "Username already signed up");
            }

            dao.save(signUpRequest.user);
            return new SignUpResponse(true, "");
        } catch (PersistentOperationException e) {
            return new SignUpResponse(false, "Server error");
        }

    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SIGNUP;
    }
}
