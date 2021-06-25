package com.mans.sbugram.models.factories;

import com.mans.sbugram.models.Post;
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
            case ADD_COMMENT:
                return Optional.ofNullable(newAddCommentRequest(data));
            case SET_LIKE:
                return Optional.ofNullable(newSetLikeRequest(data));
            case USER_POSTS:
                return Optional.ofNullable(newUserPostsRequest(data));
            case SET_FOLLOWING:
                return Optional.ofNullable(newSetFollowingRequest(data));
            case SEND_POST:
                return Optional.ofNullable(newSendPostRequest(data));
            case REPOST:
                return Optional.ofNullable(newRepostRequest(data));
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

    private static AddCommentRequest newAddCommentRequest(JSONObject data) {
        String username;
        String password;
        int postId;
        String text;

        try {
            username = data.getString("username");
            password = data.getString("password");
            postId = data.getInt("postId");
            text = data.getString("text");
        } catch (JSONException e) {
            return null;
        }

        return new AddCommentRequest(username, password, postId, text);
    }

    private static SetLikeRequest newSetLikeRequest(JSONObject data) {
        String username;
        String password;
        int postId;
        boolean liked;

        try {
            username = data.getString("username");
            password = data.getString("password");
            postId = data.getInt("postId");
            liked = data.getBoolean("liked");
        } catch (JSONException e) {
            return null;
        }

        return new SetLikeRequest(username, password, postId, liked);
    }

    private static UserPostsRequest newUserPostsRequest(JSONObject data) {
        String username;

        try {
            username = data.getString("username");
        } catch (JSONException e) {
            return null;
        }

        return new UserPostsRequest(username);
    }

    private static SetFollowingRequest newSetFollowingRequest(JSONObject data) {
        String username;
        String password;
        String targetUserUsername;
        boolean following;

        try {
            username = data.getString("username");
            password = data.getString("password");
            targetUserUsername = data.getString("targetUserUsername");
            following = data.getBoolean("following");
        } catch (JSONException e) {
            return null;
        }

        return new SetFollowingRequest(username, password, targetUserUsername, following);
    }

    private static SendPostRequest newSendPostRequest(JSONObject data) {
        String username;
        String password;
        Post post;

        try {
            username = data.getString("username");
            password = data.getString("password");

            JSONObject postJSONObject = data.getJSONObject("post");

            post = new Post(
                    postJSONObject.getInt("id"),
                    postJSONObject.getLong("postedTime"),
                    postJSONObject.getString("title"),
                    postJSONObject.getString("content"),
                    postJSONObject.getString("photoFilename"),
                    username,
                    Collections.emptySet(),
                    Collections.emptySet()
            );
        } catch (JSONException e) {
            return null;
        }

        return new SendPostRequest(username, password, post);
    }

    private static RepostRequest newRepostRequest(JSONObject data) {
        String username;
        String password;
        int repostedPostId;

        try {
            username = data.getString("username");
            password = data.getString("password");
            repostedPostId = data.getInt("repostedPostId");
        } catch (JSONException e) {
            return null;
        }

        return new RepostRequest(username, password, repostedPostId);
    }
}
