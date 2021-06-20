package com.mans.sbugram.models.responses;

import com.mans.sbugram.models.Post;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserTimelineResponse extends Response {

    public final boolean successful;
    public final String message;
    public final List<Post> timelinePosts;

    public UserTimelineResponse(boolean successful, String message, List<Post> timelinePosts) {
        this.successful = successful;
        this.message = message;
        this.timelinePosts = Collections.unmodifiableList(timelinePosts);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();

        data.put("timeline_posts", new JSONArray(
                timelinePosts.stream()
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
        return ResponseType.USER_TIMELINE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTimelineResponse that = (UserTimelineResponse) o;
        return successful == that.successful && message.equals(that.message) && timelinePosts.equals(that.timelinePosts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(successful, message, timelinePosts);
    }
}
