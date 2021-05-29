package com.mans.sbugram.server.models.responses;

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

}
