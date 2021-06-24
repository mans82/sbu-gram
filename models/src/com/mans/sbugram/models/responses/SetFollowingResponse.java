package com.mans.sbugram.models.responses;

import org.json.JSONObject;

import java.util.Objects;

public class SetFollowingResponse extends Response {

    public final boolean successful;
    public final String message;
    public final boolean following;

    public SetFollowingResponse(boolean successful, String message, boolean following) {
        this.successful = successful;
        this.message = message;
        this.following = following;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();

        data.put("following", following);

        result.put("response_type", this.getResponseType().name());
        result.put("successful", successful);
        result.put("message", message);
        result.put("data", data);

        return result;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.SET_FOLLOWING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetFollowingResponse response = (SetFollowingResponse) o;
        return successful == response.successful && following == response.following && message.equals(response.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(successful, message, following);
    }
}
