package com.mans.sbugram.models.responses;

import org.json.JSONObject;

import java.util.Objects;

public class SetLikeResponse extends Response {

    public final boolean successful;
    public final String message;
    public final boolean liked;

    public SetLikeResponse(boolean successful, String message, boolean liked) {
        this.successful = successful;
        this.message = message;
        this.liked = liked;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();

        data.put("liked", liked);

        result.put("response_type", this.getResponseType().name());
        result.put("successful", successful);
        result.put("message", message);
        result.put("data", data);

        return result;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.SET_LIKE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetLikeResponse that = (SetLikeResponse) o;
        return successful == that.successful && liked == that.liked && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(successful, message, liked);
    }
}
