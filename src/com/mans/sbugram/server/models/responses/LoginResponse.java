package com.mans.sbugram.server.models.responses;

import org.json.JSONObject;

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
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("successful", this.successful);
        result.put("message", this.message);

        return result;
    }
}
