package com.mans.sbugram.server.models.requests;

import com.mans.sbugram.server.models.User;
import org.json.JSONObject;

import java.util.Objects;

public class SignUpRequest extends Request{

    public final User user;

    public SignUpRequest(User user) {
        this.user = user;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("request_type", this.getRequestType().name());
        result.put("data", this.user.toJSON());

        return result;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SIGNUP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignUpRequest that = (SignUpRequest) o;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
