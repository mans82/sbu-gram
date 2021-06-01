package com.mans.sbugram.models.responses;

import com.sun.istack.internal.NotNull;
import org.json.JSONObject;

import java.util.Objects;

public class SignUpResponse extends Response{

    public final boolean successful;
    public final String message;

    public SignUpResponse(boolean successful, @NotNull String message) {
        this.successful = successful;
        this.message = message;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("response_type", this.getResponseType().name());
        result.put("successful", this.successful);
        result.put("message", this.message);

        return result;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.SIGNUP;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignUpResponse that = (SignUpResponse) o;
        return successful == that.successful && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(successful, message);
    }
}
