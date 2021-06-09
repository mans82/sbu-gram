package com.mans.sbugram.models.factories;

import com.mans.sbugram.models.responses.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class ResponseFactory {

    public static Optional<Response> getResponse(JSONObject object) {
        ResponseType responseType;
        boolean successful;
        String message;
        JSONObject data;
        try {
            responseType = ResponseType.valueOf(object.getString("response_type"));
            successful = object.getBoolean("successful");
        } catch (JSONException | IllegalArgumentException e) {
            return Optional.empty();
        }

        try {
            message = object.getString("message");
        } catch (JSONException e) {
            message = "";
        }

        try {
            data = object.getJSONObject("data");
        } catch (JSONException e) {
            data = null;
        }

        switch (responseType) {
            case LOGIN:
                return Optional.of(newLoginResponse(successful, message));
            case SIGNUP:
                return Optional.of(newSignUpResponse(successful, message));
            case FILE_UPLOAD:
                return Optional.ofNullable(newFileUploadResponse(successful, message, data));
            case FILE_DOWNLOAD:
                return Optional.ofNullable(newFileDownloadResponse(successful, message, data));
        }

        return Optional.empty();

    }

    private static LoginResponse newLoginResponse(boolean successful, String message) {
        return new LoginResponse(successful, message);
    }

    private static SignUpResponse newSignUpResponse(boolean successful, String message) {
        return new SignUpResponse(successful, message);
    }

    private static FileUploadResponse newFileUploadResponse(boolean successful, String message, JSONObject data) {
        if (data == null) {
            return null;
        }

        String filename;

        try {
            filename = data.getString("filename");
        } catch (JSONException e) {
            return null;
        }

        return new FileUploadResponse(successful, message, filename);
    }

    private static FileDownloadResponse newFileDownloadResponse(boolean successful, String message, JSONObject data) {
        if (data == null) {
            return null;
        }

        String blob;

        try {
            blob = data.getString("blob");
        } catch (JSONException e) {
            return null;
        }

        return new FileDownloadResponse(successful, message, blob);
    }

}
