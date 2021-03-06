package com.mans.sbugram.server.events;

import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.requests.SignUpRequest;
import com.mans.sbugram.models.responses.Response;
import com.mans.sbugram.models.responses.SignUpResponse;

import java.time.Instant;

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
            if (signUpRequest.user.username.length() < 4 || signUpRequest.user.username.length() > 16) {
                return new SignUpResponse(false, "Username should have 4 to 16 characters");
            }

            if (!signUpRequest.user.password.matches("[A-Za-z0-9]{8,}")) {
                return new SignUpResponse(false, "Invalid password (should be at least 8 characters and only contain letters and numbers)");
            }

            if (dao.get(signUpRequest.user.username).isPresent()) {
                return new SignUpResponse(false, "Username already signed up");
            }

            dao.save(signUpRequest.user);

            System.out.printf(
                    "%s signed up\nProfile Image: %s\nTime: %s\n\n",
                    signUpRequest.user.username,
                    signUpRequest.user.profilePhotoFilename.isEmpty() ? "<none>" : signUpRequest.user.profilePhotoFilename,
                    this.formatDate(Instant.now())
            );
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
