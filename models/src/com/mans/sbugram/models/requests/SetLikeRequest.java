package com.mans.sbugram.models.requests;

import org.json.JSONObject;

import java.util.Objects;

public class SetLikeRequest extends Request {

    public final String username;
    public final String password;
    public final int postId;
    public final boolean liked;

    public SetLikeRequest(String username, String password, int postId, boolean liked) {
        this.username = username;
        this.password = password;
        this.postId = postId;
        this.liked = liked;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();

        data.put("username", username);
        data.put("password", password);
        data.put("postId", postId);
        data.put("liked", liked);

        result.put("request_type", this.getRequestType().name());
        result.put("data", data);

        return result;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SET_LIKE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetLikeRequest that = (SetLikeRequest) o;
        return postId == that.postId && liked == that.liked && username.equals(that.username) && password.equals(that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, postId, liked);
    }
}
