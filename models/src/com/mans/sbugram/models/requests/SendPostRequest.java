package com.mans.sbugram.models.requests;

import com.mans.sbugram.models.Post;
import org.json.JSONObject;

import java.util.Objects;

public class SendPostRequest extends Request {

    public final String username;
    public final String password;
    public final Post post;

    public SendPostRequest(String username, String password, Post post) {
        this.username = username;
        this.password = password;
        this.post = post;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();

        data.put("username", username);
        data.put("password", password);
        data.put("post", post.toJSON());

        result.put("request_type", this.getRequestType().name());
        result.put("data", data);

        return result;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SEND_POST;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SendPostRequest that = (SendPostRequest) o;
        return username.equals(that.username) && password.equals(that.password) && post.equals(that.post);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, post);
    }
}
