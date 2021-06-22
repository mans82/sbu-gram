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
            case USER_TIMELINE:
                return Optional.ofNullable(newUserTimelineRequest(data));
            case USER_INFO:
                return Optional.ofNullable(newUserInfoRequest(data));
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

    private static SignUpRequest newSignUpRequest(JSONObject data) {
        String name, username, password, city, bio, profilePhotoFilename;

        try {
            name = data.getString("name");
            username = data.getString("username");
            password = data.getString("password");
            city = data.getString("city");
            bio = data.getString("bio");
            profilePhotoFilename = data.getString("profilePhotoFilename");

            return new SignUpRequest(
                    new User(username, name, password, city, bio, profilePhotoFilename, Collections.emptySet())
            );
        } catch (JSONException e) {
            return null;
        }
    }

    private static FileUploadRequest newFileUploadRequest(JSONObject data) {
        String blob;

        try {
            blob = data.getString("blob");
        } catch (JSONException e) {
            return null;
        }

        return new FileUploadRequest(blob);
    }

    private static FileDownloadRequest newFileDownloadRequest(JSONObject data) {
        String filename;

        try {
            filename = data.getString("filename");
        } catch (JSONException e) {
            return null;
        }

        return new FileDownloadRequest(filename);
    }

    private static UserTimelineRequest newUserTimelineRequest(JSONObject data) {
        String username;
        String password;
        long fromTime;
        int count;

        try {
            username = data.getString("username");
            password = data.getString("password");
            fromTime = data.getLong("fromtime");
            count = data.getInt("count");
        } catch (JSONException e) {
            return null;
        }

        return new UserTimelineRequest(username, password, fromTime, count);
    }

    private static UserInfoRequest newUserInfoRequest(JSONObject data) {
        String username;

        try {
            username = data.getString("username");
        } catch (JSONException e) {
            return null;
        }

        return new UserInfoRequest(username);
    }

}
