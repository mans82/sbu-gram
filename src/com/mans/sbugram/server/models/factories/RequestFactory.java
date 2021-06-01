package com.mans.sbugram.server.models.factories;

import com.mans.sbugram.server.models.User;
import com.mans.sbugram.server.models.requests.LoginRequest;
import com.mans.sbugram.server.models.requests.Request;
import com.mans.sbugram.server.models.requests.RequestType;
import com.mans.sbugram.server.models.requests.SignUpRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class RequestFactory {

    public static Optional<Request> getRequest(JSONObject object) {

        String requestTypeString;
        try {
            requestTypeString = object.getString("request_type");
        } catch (JSONException e) {
            return Optional.empty();
        }

        RequestType type;
        try {
            type = RequestType.valueOf(requestTypeString);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        JSONObject data;
        try {
            data = object.getJSONObject("data");
        } catch (JSONException e) {
            return Optional.empty();
        }

        switch (type) {
            case SIGNUP:
                return Optional.ofNullable(newSignUpRequest(data));
            case LOGIN:
                return Optional.ofNullable(newLoginRequest(data));
        }

        return Optional.empty();
    }

    private static LoginRequest newLoginRequest(JSONObject object) {
        String username, password;
        try {
            username = object.getString("username");
            password = object.getString("password");

            return new LoginRequest(username, password);
        } catch (JSONException e) {
            return null;
        }
    }

    private static SignUpRequest newSignUpRequest(JSONObject object) {
        String name, username, password, city, bio;

        try {
            name = object.getString("name");
            username = object.getString("username");
            password = object.getString("password");
            city = object.getString("city");
            bio = object.getString("bio");

            return new SignUpRequest(
                    new User(username, name, password, city, bio)
            );
        } catch (JSONException e) {
            return null;
        }
    }

}
