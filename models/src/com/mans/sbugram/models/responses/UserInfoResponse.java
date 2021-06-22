package com.mans.sbugram.models.responses;

import com.mans.sbugram.models.User;
import org.json.JSONObject;

import java.util.Objects;

public class UserInfoResponse extends Response {

    public final boolean successful;
    public final String message;
    public final User user;

    public UserInfoResponse(boolean successful, String message, User user) {
        this.successful = successful;
        this.message = message;
        this.user = user;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();

        data.put("user", user != null ? user.toJSON() : new JSONObject());

        result.put("response_type", this.getResponseType().name());
        result.put("successful", successful);
        result.put("message", message);
        result.put("data", data);

        return result;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.USER_INFO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfoResponse response = (UserInfoResponse) o;
        return successful == response.successful && message.equals(response.message) && Objects.equals(user, response.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(successful, message, user);
    }
}
