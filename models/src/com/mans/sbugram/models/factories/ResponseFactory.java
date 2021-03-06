package com.mans.sbugram.models.factories;

import com.mans.sbugram.models.Comment;
import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.responses.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
            case USER_TIMELINE:
                return Optional.ofNullable(newUserTimelineResponse(successful, message, data));
            case USER_INFO:
                return Optional.ofNullable(newUserInfoResponse(successful, message, data));
            case ADD_COMMENT:
                return Optional.of(newAddCommentResponse(successful, message));
            case SET_LIKE:
                return Optional.ofNullable(newSetLikeResponse(successful, message, data));
            case USER_POSTS:
                return Optional.ofNullable(newUserPostsResponse(successful, message, data));
            case SET_FOLLOWING:
                return Optional.ofNullable(newSetFollowingResponse(successful, message, data));
            case SEND_POST:
                return Optional.of(newSendPostResponse(successful, message));
            case REPOST:
                return Optional.of(newRepostResponse(successful, message));
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

    private static UserTimelineResponse newUserTimelineResponse(boolean successful, String message, JSONObject data) {
        if (data == null) {
            return null;
        }

        JSONArray timelinePostsJSON = data.getJSONArray("timeline_posts");
        List<Post> timelinePosts = new ArrayList<>();

        try {
            for (int i = 0; i < timelinePostsJSON.length(); i++) {
                JSONObject postJSON = timelinePostsJSON.getJSONObject(i);

                int id = postJSON.getInt("id");
                long postedTime = postJSON.getLong("postedTime");
                String title = postJSON.getString("title");
                String content = postJSON.getString("content");
                String photoFilename = postJSON.getString("photoFilename");
                String posterUsername = postJSON.getString("posterUsername");
                boolean isRepost = postJSON.getBoolean("isRepost");
                String originalPosterUsername = postJSON.getString("originalPosterUsername");

                JSONArray commentsJSONArray = postJSON.getJSONArray("comments");
                Set<Comment> comments = IntStream.range(0, commentsJSONArray.length())
                        .mapToObj(commentsJSONArray::getJSONObject)
                        .map(commentJSON -> new Comment(commentJSON.getString("username"), commentJSON.getString("text")))
                        .collect(Collectors.toSet());

                JSONArray likedUsersJSONArray = postJSON.getJSONArray("likedUsersUsernames");
                Set<String> likedUsersUsernames = IntStream.range(0, likedUsersJSONArray.length())
                        .mapToObj(likedUsersJSONArray::getString)
                        .collect(Collectors.toSet());


                timelinePosts.add(
                        new Post(id, postedTime, title, content, photoFilename, posterUsername, comments, isRepost, originalPosterUsername, likedUsersUsernames)
                );
            }
        } catch (JSONException e) {
            return null;
        }

        return new UserTimelineResponse(successful, message, timelinePosts);
    }

    private static UserInfoResponse newUserInfoResponse(boolean successful, String message, JSONObject data) {
        if (data == null) {
            return null;
        }

        JSONObject user;
        String name, username, password, city, bio, profilePhotoFilename;
        Set<String> followingUsersUsernames;

        try {
            user = data.getJSONObject("user");
            if (user.isEmpty()) {
                return new UserInfoResponse(
                        successful,
                        message,
                        null
                );
            }

            name = user.getString("name");
            username = user.getString("username");
            password = user.getString("password");
            city = user.getString("city");
            bio = user.getString("bio");
            profilePhotoFilename = user.getString("profilePhotoFilename");

            followingUsersUsernames = user.getJSONArray("followingUsersUsernames").toList().stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet());
        } catch (JSONException e) {
            return null;
        }

        return new UserInfoResponse(
                successful,
                message,
                new User(username, name, password, city, bio, profilePhotoFilename, followingUsersUsernames)
        );
    }

    private static AddCommentResponse newAddCommentResponse(boolean successful, String message) {
        return new AddCommentResponse(successful, message);
    }

    private static SetLikeResponse newSetLikeResponse(boolean successful, String message, JSONObject data) {
        if (data == null) {
            return null;
        }

        boolean liked;

        try {
            liked = data.getBoolean("liked");
        } catch (JSONException e) {
            return null;
        }

        return new SetLikeResponse(successful, message, liked);
    }

    private static UserPostsResponse newUserPostsResponse(boolean successful, String message, JSONObject data) {
        if (data == null) {
            return null;
        }

        List<Post> posts;
        JSONArray postsJSONArray;

        try {
            postsJSONArray = data.getJSONArray("posts");
        } catch (JSONException e) {
            return null;
        }

        posts = IntStream.range(0, postsJSONArray.length())
                .mapToObj(postsJSONArray::getJSONObject)
                .map(postJSONObject -> {
                    try {
                        JSONArray commentsJSONArray = postJSONObject.getJSONArray("comments");
                        Set<Comment> comments = IntStream.range(0, commentsJSONArray.length())
                                .mapToObj(commentsJSONArray::getJSONObject)
                                .map(commentJSONObject -> {
                                    try {
                                        return new Comment(
                                                commentJSONObject.getString("username"),
                                                commentJSONObject.getString("text")
                                        );
                                    } catch (JSONException e) {
                                        return null;
                                    }
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet());

                        Set<String> likedUsersUsernames = postJSONObject.getJSONArray("likedUsersUsernames").toList().stream()
                            .map(String::valueOf)
                            .collect(Collectors.toSet());

                        return new Post(
                                postJSONObject.getInt("id"),
                                postJSONObject.getLong("postedTime"),
                                postJSONObject.getString("title"),
                                postJSONObject.getString("content"),
                                postJSONObject.getString("photoFilename"),
                                postJSONObject.getString("posterUsername"),
                                comments,
                                postJSONObject.getBoolean("isRepost"),
                                postJSONObject.getString("originalPosterUsername"),
                                likedUsersUsernames
                        );
                    } catch (JSONException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new UserPostsResponse(successful, message, posts);
    }

    private static SetFollowingResponse newSetFollowingResponse(boolean successful, String message, JSONObject data) {
        if (data == null) {
            return null;
        }

        boolean following;

        try {
            following = data.getBoolean("following");
        } catch (JSONException e) {
            return null;
        }

        return new SetFollowingResponse(successful, message, following);
    }

    private static SendPostResponse newSendPostResponse(boolean successful, String message) {
        return new SendPostResponse(successful, message);
    }

    private static RepostResponse newRepostResponse(boolean successful, String message) {
        return new RepostResponse(successful, message);
    }
}
