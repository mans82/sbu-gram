package com.mans.sbugram.models.requests;

import org.json.JSONObject;

import java.util.Objects;

public class SetFollowingRequest extends Request {

    public final String username;
    public final String password;
    public final String targetUserUsername;
    public final boolean following;

    public SetFollowingRequest(String username, String password, String targetUserUsername, boolean following) {
        this.username = username;
        this.password = password;
        this.targetUserUsername = targetUserUsername;
        this.following = following;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();

        data.put("username", username);
        data.put("password", password);
        data.put("targetUserUsername", targetUserUsername);
        data.put("following", following);

        result.put("request_type", this.getRequestType().name());
        result.put("data", data);

        return result;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SET_FOLLOWING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetFollowingRequest that = (SetFollowingRequest) o;
        return following == that.following && username.equals(that.username) && password.equals(that.password) && targetUserUsername.equals(that.targetUserUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, targetUserUsername, following);
    }
}
