package com.mans.sbugram.server.models.factories;

import com.mans.sbugram.server.models.responses.LoginResponse;
import com.mans.sbugram.server.models.responses.Response;
import com.mans.sbugram.server.models.responses.ResponseType;
import com.mans.sbugram.server.models.responses.SignUpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class ResponseFactory {

    public static Optional<Response> getResponse(JSONObject object) {
        ResponseType responseType;
        boolean successful;
        String message;
        try {
            responseType = ResponseType.valueOf(object.getString("response_type"));
            successful = object.getBoolean("successful");
        } catch (JSONException | IllegalArgumentException e) {
            return Optional.empty();
        }

        try {
            message = object.getString("message");
        } catch (JSONException e) {
            message = "";
        }

        switch (responseType) {
            case LOGIN:
                return Optional.of(newLoginResponse(successful, message));
            case SIGNUP:
                return Optional.of(newSignUpResponse(successful, message));
        }

        return Optional.empty();

    }

    private static LoginResponse newLoginResponse(boolean successful, String message) {
        return new LoginResponse(successful, message);
    }

    private static SignUpResponse newSignUpResponse(boolean successful, String message) {
        return new SignUpResponse(successful, message);
    }

}
