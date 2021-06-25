package com.mans.sbugram.models.responses;

import org.json.JSONObject;

import java.util.Objects;

public class SendPostResponse extends Response {

    public final boolean successful;
    public final String message;

    public SendPostResponse(boolean successful, String message) {
        this.successful = successful;
        this.message = message;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("response_type", this.getResponseType().name());
        result.put("successful", successful);
        result.put("message", message);

        return result;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.SEND_POST;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SendPostResponse that = (SendPostResponse) o;
        return successful == that.successful && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(successful, message);
    }
}
