package com.mans.sbugram.models.responses;

import com.mans.sbugram.models.Post;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserPostsResponse extends Response {

    public final boolean successful;
    public final String message;
    public final List<Post> posts;

    public UserPostsResponse(boolean successful, String message, List<Post> posts) {
        this.successful = successful;
        this.message = message;
        this.posts = Collections.unmodifiableList(posts);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();

        data.put("posts", new JSONArray(
                posts.stream()
                .map(Post::toJSON)
                .collect(Collectors.toList())
        ));

        result.put("response_type", this.getResponseType().name());
        result.put("successful", successful);
        result.put("message", message);
        result.put("data", data);

        return result;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.USER_POSTS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPostsResponse response = (UserPostsResponse) o;
        return successful == response.successful && message.equals(response.message) && posts.equals(response.posts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(successful, message, posts);
    }
}
