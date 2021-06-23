package com.mans.sbugram.models.requests;

import org.json.JSONObject;

import java.util.Objects;

public class AddCommentRequest extends Request {

    public final String username;
    public final String password;
    public final int postId;
    public final String text;

    public AddCommentRequest(String username, String password, int postId, String text) {
        this.username = username;
        this.password = password;
        this.postId = postId;
        this.text = text;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();

        data.put("username", username);
        data.put("password", password);
        data.put("postId", postId);
        data.put("text", text);

        result.put("request_type", this.getRequestType().name());
        result.put("data", data);

        return result;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.ADD_COMMENT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddCommentRequest that = (AddCommentRequest) o;
        return postId == that.postId && username.equals(that.username) && password.equals(that.password) && text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, postId, text);
    }
}
