package com.mans.sbugram.models.factories;

import com.mans.sbugram.models.Post;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.responses.*;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.Assert.*;

public class ResponseFactoryTest {

    @Test
    public void testGetResponseNoResponseType() {
        JSONObject object = new JSONObject();

        object.put("success", true);
        object.put("message", "Do you know who I am?");

        assertFalse(ResponseFactory.getResponse(object).isPresent());
    }

    @Test
    public void testGetResponseNoSuccess() {
        JSONObject object = new JSONObject();

        object.put("response_type", ResponseType.LOGIN.name());
        object.put("message", "Am I successful?");

        assertFalse(ResponseFactory.getResponse(object).isPresent());
    }

    @Test
    public void testGetResponseNoMessage() {
        LoginResponse testLoginResponse = new LoginResponse(true, "");
        JSONObject object = new JSONObject();

        object.put("response_type", ResponseType.LOGIN.name());
        object.put("successful", true);

        Optional<Response> parsedResponse = ResponseFactory.getResponse(object);

        assertTrue(parsedResponse.isPresent());
        assertEquals(testLoginResponse, parsedResponse.get());
    }

    @Test
    public void testGetResponseLoginResponse() {
        LoginResponse testLoginResponse = new LoginResponse(true, "Logged in");
        Optional<Response> parsedResponse = ResponseFactory.getResponse(testLoginResponse.toJSON());

        assertTrue(parsedResponse.isPresent());
        assertEquals(testLoginResponse, parsedResponse.get());
    }

    @Test
    public void testGetResponseSignUpResponse() {
        SignUpResponse testSignUpResponse = new SignUpResponse(true, "");
        Optional<Response> parsedResponse = ResponseFactory.getResponse(testSignUpResponse.toJSON());

        assertTrue(parsedResponse.isPresent());
        assertEquals(testSignUpResponse, parsedResponse.get());
    }

    @Test
    public void testGetResponseFileUploadResponse() {
        FileUploadResponse testFileUploadResponse = new FileUploadResponse(true, "Some message", "newFile");
        Optional<Response> parsedResponse = ResponseFactory.getResponse(testFileUploadResponse.toJSON());

        assertTrue(parsedResponse.isPresent());
        assertEquals(
                testFileUploadResponse,
                parsedResponse.get()
        );
    }

    @Test
    public void testGetResponseFileUploadResponseInvalidData() {
        JSONObject invalidResponse = new JSONObject("{\"response_type\": \"FILE_UPLOAD\", \"successful\": true, \"message\": \"\", \"data\": {}}");
        Optional<Response> parsedResponse = ResponseFactory.getResponse(invalidResponse);

        assertFalse(parsedResponse.isPresent());
    }

    @Test
    public void testGetResponseFileUploadResponseNoData() {
        JSONObject invalidResponse = new JSONObject("{\"response_type\": \"FILE_UPLOAD\", \"successful\": true, \"message\": \"\"}");
        Optional<Response> parsedResponse = ResponseFactory.getResponse(invalidResponse);

        assertFalse(parsedResponse.isPresent());
    }

    @Test
    public void testGetResponseFileDownloadResponse() {
        FileDownloadResponse testFileDownloadResponse = new FileDownloadResponse(true, "Some message", "blobblob");
        Optional<Response> parsedResponse = ResponseFactory.getResponse(testFileDownloadResponse.toJSON());

        assertTrue(parsedResponse.isPresent());
        assertEquals(
                testFileDownloadResponse,
                parsedResponse.get()
        );
    }

    @Test
    public void testGetResponseFileDownloadResponseInvalidData() {
        JSONObject invalidResponse = new JSONObject("{\"response_type\": \"FILE_DOWNLOAD\", \"successful\": true, \"message\": \"\", \"data\": {}}");
        Optional<Response> parsedResponse = ResponseFactory.getResponse(invalidResponse);

        assertFalse(parsedResponse.isPresent());
    }

    @Test
    public void testGetResponseFileDownloadResponseNoData() {
        JSONObject invalidResponse = new JSONObject("{\"response_type\": \"FILE_DOWNLOAD\", \"successful\": true, \"message\": \"\"}");
        Optional<Response> parsedResponse = ResponseFactory.getResponse(invalidResponse);

        assertFalse(parsedResponse.isPresent());
    }

    @Test
    public void testGetResponseUserTimelineResponse() {
        UserTimelineResponse testUserTimelineResponse = new UserTimelineResponse(
                true,
                "some message",
                Arrays.asList(
                        new Post(0, 12, "title1", "content1", "", "jafar", Collections.emptySet(), Collections.emptySet()),
                        new Post(1, 14, "title2", "content2", "", "akbar", Collections.emptySet(), Collections.emptySet())
                )
        );

        Optional<Response> parsedResponse = ResponseFactory.getResponse(testUserTimelineResponse.toJSON());

        assertTrue(parsedResponse.isPresent());
        assertEquals(testUserTimelineResponse, parsedResponse.get());
    }

    @Test
    public void testGetResponseUserTimelineResponseInvalidData() {
        JSONObject invalidResponse = new JSONObject("{\"data\":{\"timeline_posts\":[{\"photoFilename\":\"\",\"isRepost\":false,\"id\":0,\"title\":\"title1\",\"content\":\"content1\",\"posterUsername\":\"jafar\",\"repostedPostId\":-1},{\"photoFilename\":\"\",\"isRepost\":false,\"postedTime\":14,\"id\":1,\"title\":\"title2\",\"content\":\"content2\",\"posterUsername\":\"akbar\",\"repostedPostId\":-1}]},\"response_type\":\"USER_TIMELINE\",\"message\":\"some message\",\"successful\":true}");
        Optional<Response> parsedResponse = ResponseFactory.getResponse(invalidResponse);

        assertFalse(parsedResponse.isPresent());
    }

    @Test
    public void testGetResponseUserInfoResponse() {
        UserInfoResponse response = new UserInfoResponse(
                true,
                "some message",
                new User("jafar", "Jafar", "1234", "", "", "", new HashSet<>(Arrays.asList("asghar", "akbar")))
        );
        Optional<Response> parsedResponse = ResponseFactory.getResponse(response.toJSON());

        assertTrue(parsedResponse.isPresent());
        UserInfoResponse castedParsedResponse = (UserInfoResponse) parsedResponse.get();
        assertEquals(
                response,
                castedParsedResponse
        );
        assertEquals(2, castedParsedResponse.user.followingUsersUsernames.size());
        assertTrue(castedParsedResponse.user.followingUsersUsernames.contains("akbar"));
    }

    @Test
    public void testGetResponseUserInfoResponseInvalidData() {
        JSONObject invalidResponse = new JSONObject("{\"data\":{\"user\":{\"city\":\"\",\"followingUsersUsernames\":[\"akbar\",\"asghar\"],\"name\":\"Jafar\",\"bio\":\"\",\"profilePhotoFilename\":\"\",\"username\":\"jafar\"}},\"response_type\":\"USER_INFO\",\"message\":\"some message\",\"successful\":true}");

        assertFalse(ResponseFactory.getResponse(invalidResponse).isPresent());
    }

    @Test
    public void testGetResponseUserInfoResponseNoUser() {
        UserInfoResponse response = new UserInfoResponse(
                false,
                "some message",
                null
        );
        Optional<Response> parsedResponse = ResponseFactory.getResponse(response.toJSON());

        assertTrue(parsedResponse.isPresent());
        assertEquals(
                response,
                parsedResponse.get()
        );
    }

    @Test
    public void testGetResponseAddCommentResponse() {
        AddCommentResponse response = new AddCommentResponse(true, "some message");
        Optional<Response> parsedResponse = ResponseFactory.getResponse(response.toJSON());

        assertTrue(parsedResponse.isPresent());
        assertEquals(response, parsedResponse.get());
    }

    @Test
    public void testGetResponseSetLikeResponse() {
        SetLikeResponse response = new SetLikeResponse(true, "somemessage", false);

        Optional<Response> parsedResponse = ResponseFactory.getResponse(response.toJSON());

        assertTrue(parsedResponse.isPresent());
        assertEquals(
                response,
                parsedResponse.get()
        );
    }

    @Test
    public void testGetResponseSetLikeResponseInvalidData() {
        JSONObject invalidResponse = new JSONObject("{\"data\":{},\"response_type\":\"SET_LIKE\",\"message\":\"somemessage\",\"successful\":true}");

        assertFalse(ResponseFactory.getResponse(invalidResponse).isPresent());
    }

    @Test
    public void testGetResponseUserPosts() {
        UserPostsResponse response = new UserPostsResponse(true, "some message", Arrays.asList(
                new Post(0, 12, "title1", "content1", "", "jafar", Collections.emptySet(), Collections.emptySet()),
                new Post(1, 14, "title2", "content2", "", "akbar", Collections.emptySet(), Collections.emptySet())
        ));

        Optional<Response> parsedResponse = ResponseFactory.getResponse(response.toJSON());

        assertTrue(parsedResponse.isPresent());
        assertEquals(
                response,
                parsedResponse.get()
        );
    }

    @Test
    public void testGetResponseUserPostsInvalidData() {
        JSONObject invalidResponseJSON = new JSONObject("{\"data\":{\"posts\":[{\"comments\":[],\"isRepost\":false,\"postedTime\":12,\"id\":0,\"title\":\"title1\",\"likedUsersUsernames\":[],\"content\":\"content1\",\"posterUsername\":\"jafar\",\"repostedPostId\":-1},{\"photoFilename\":\"\",\"comments\":[],\"isRepost\":false,\"postedTime\":14,\"id\":1,\"title\":\"title2\",\"likedUsersUsernames\":[],\"content\":\"content2\",\"posterUsername\":\"akbar\",\"repostedPostId\":-1}]},\"response_type\":\"USER_POSTS\",\"message\":\"some message\",\"successful\":true}");

        Optional<Response> response = ResponseFactory.getResponse(invalidResponseJSON);

        assertTrue(response.isPresent());
        assertEquals(1, ((UserPostsResponse)response.get()).posts.size());
    }
}