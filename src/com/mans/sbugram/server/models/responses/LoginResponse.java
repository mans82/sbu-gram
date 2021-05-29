package com.mans.sbugram.server.models.responses;

import org.json.JSONObject;

import java.util.Objects;

public class LoginResponse extends Response {

    public final boolean successful;
    public final String message;

    public LoginResponse(boolean successful, String message) {
        this.successful = successful;
        this.message = message;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.LOGIN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginResponse that = (LoginResponse) o;
        return successful == that.successful && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(successful, message);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("successful", this.successful);
        result.put("message", this.message);

        return result;
    }
}
