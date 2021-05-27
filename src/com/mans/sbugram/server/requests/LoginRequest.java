package com.mans.sbugram.server.requests;

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

}
