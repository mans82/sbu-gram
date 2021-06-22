package com.mans.sbugram.models.requests;

import org.json.JSONObject;

import java.util.Objects;

public class UserInfoRequest extends Request {

    public final String username;

    public UserInfoRequest(String username) {
        this.username = username;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();

        data.put("username", username);

        result.put("request_type", this.getRequestType().name());
        result.put("data", data);

        return result;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.USER_INFO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfoRequest request = (UserInfoRequest) o;
        return username.equals(request.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
