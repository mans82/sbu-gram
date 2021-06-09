package com.mans.sbugram.models.responses;

import org.json.JSONObject;

import java.util.Objects;

public class FileDownloadResponse extends Response {

    public final boolean successful;
    public final String message;
    public final String blob;

    public FileDownloadResponse(boolean successful, String message, String blob) {
        this.successful = successful;
        this.message = message;
        this.blob = blob;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("response_type", this.getResponseType().name());
        result.put("successful", successful);
        result.put("message", message);
        result.put("data", new JSONObject().put("blob", blob));

        return result;
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.FILE_DOWNLOAD;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileDownloadResponse that = (FileDownloadResponse) o;
        return successful == that.successful && message.equals(that.message) && blob.equals(that.blob);
    }

    @Override
    public int hashCode() {
        return Objects.hash(successful, message, blob);
    }
}
