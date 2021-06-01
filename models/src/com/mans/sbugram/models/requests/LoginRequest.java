package com.mans.sbugram.models.requests;

import org.json.JSONObject;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginRequest request = (LoginRequest) o;
        return username.equals(request.username) && password.equals(request.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
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
