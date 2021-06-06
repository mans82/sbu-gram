package com.mans.sbugram.models.requests;

import org.json.JSONObject;

import java.util.Objects;

public class FileUploadRequest extends Request {

    public final String blob;

    public FileUploadRequest(String blob) {
        this.blob = blob;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("request_type", this.getRequestType().name());
        result.put("data", new JSONObject().put("blob", blob));

        return result;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.FILE_UPLOAD;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileUploadRequest request = (FileUploadRequest) o;
        return blob.equals(request.blob);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blob);
    }
}
