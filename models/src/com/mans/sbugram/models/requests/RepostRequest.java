package com.mans.sbugram.models.requests;

import org.json.JSONObject;

import java.util.Objects;

public class RepostRequest extends Request {

    public final String username;
    public final String password;
    public final int repostedPostId;

    public RepostRequest(String username, String password, int repostedPostId) {
        this.username = username;
        this.password = password;
        this.repostedPostId = repostedPostId;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();

        data.put("username", username);
        data.put("password", password);
        data.put("repostedPostId", repostedPostId);

        result.put("request_type", this.getRequestType().name());
        result.put("data", data);

        return result;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.REPOST;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepostRequest that = (RepostRequest) o;
        return repostedPostId == that.repostedPostId && username.equals(that.username) && password.equals(that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, repostedPostId);
    }
}
