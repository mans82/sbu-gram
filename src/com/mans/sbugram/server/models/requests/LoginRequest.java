package com.mans.sbugram.server.models.requests;

import org.json.JSONObject;

public class LoginRequest extends Request{

    public final String username;
    public final String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.LOGIN;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();

        data.put("username", this.username);
        data.put("password", this.password);

        result.put("request_type", this.getRequestType().name());
        result.put("data", data);

        return result;
    }
}
