package com.mans.sbugram.models.responses;

import org.json.JSONObject;

import java.util.Objects;

public class FileUploadResponse extends Response {

    public final boolean successful;
    public final String message;
    public final String filename;

    public FileUploadResponse(boolean successful, String message, String filename) {
        this.successful = successful;
        this.message = message;
        this.filename = filename;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("response_type", this.getResponseType().name());
        result.put("successful", successful);
        result.put("message", message);
        result.put("data", new JSONObject().put("filename", filename));

        return result;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.FILE_UPLOAD;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileUploadResponse response = (FileUploadResponse) o;
        return successful == response.successful && message.equals(response.message) && filename.equals(response.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(successful, message, filename);
    }
}
