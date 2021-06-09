package com.mans.sbugram.models.requests;

import org.json.JSONObject;

import java.util.Objects;

public class FileDownloadRequest extends Request {

    public final String filename;

    public FileDownloadRequest(String filename) {
        this.filename = filename;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("request_type", this.getRequestType().name());
        result.put("data", new JSONObject().put("filename", filename));

        return result;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.FILE_DOWNLOAD;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileDownloadRequest request = (FileDownloadRequest) o;
        return filename.equals(request.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename);
    }
}
