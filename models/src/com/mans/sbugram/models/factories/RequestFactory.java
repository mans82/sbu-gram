package com.mans.sbugram.models.factories;

import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Optional;

public class RequestFactory {

    public static Optional<Request> getRequest(JSONObject object) {

        String requestTypeString;
        try {
            requestTypeString = object.getString("request_type");
        } catch (JSONException e) {
            return Optional.empty();
        }

        RequestType type;
        try {
            type = RequestType.valueOf(requestTypeString);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }

        JSONObject data;
        try {
            data = object.getJSONObject("data");
        } catch (JSONException e) {
            return Optional.empty();
        }

        switch (type) {
            case SIGNUP:
                return Optional.ofNullable(newSignUpRequest(data));
            case LOGIN:
                return Optional.ofNullable(newLoginRequest(data));
            case FILE_UPLOAD:
                return Optional.ofNullable(newFileUploadRequest(data));
            case FILE_DOWNLOAD:
                return Optional.ofNullable(newFileDownloadRequest(data));
        }

        return Optional.empty();
    }

    private static LoginRequest newLoginRequest(JSONObject object) {
        String username, password;
        try {
            username = object.getString("username");
            password = object.getString("password");

            return new LoginRequest(username, password);
        } catch (JSONException e) {
            return null;
        }
    }

    private static SignUpRequest newSignUpRequest(JSONObject object) {
        String name, username, password, city, bio, profilePhotoFilename;

        try {
            name = object.getString("name");
            username = object.getString("username");
            password = object.getString("password");
            city = object.getString("city");
            bio = object.getString("bio");
            profilePhotoFilename = object.getString("profilePhotoFilename");

            return new SignUpRequest(
                    new User(username, name, password, city, bio, profilePhotoFilename, Collections.emptySet())
            );
        } catch (JSONException e) {
            return null;
        }
    }

    private static FileUploadRequest newFileUploadRequest(JSONObject object) {
        String blob;

        try {
            blob = object.getString("blob");
        } catch (JSONException e) {
            return null;
        }

        return new FileUploadRequest(blob);
    }

    private static FileDownloadRequest newFileDownloadRequest(JSONObject object) {
        String filename;

        try {
            filename = object.getString("filename");
        } catch (JSONException e) {
            return null;
        }

        return new FileDownloadRequest(filename);
    }

}
