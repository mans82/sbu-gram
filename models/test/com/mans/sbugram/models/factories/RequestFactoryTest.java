package com.mans.sbugram.models.factories;

import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.*;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class RequestFactoryTest {

    @Test
    public void testGetRequestNoRequestType() {
        JSONObject object = new JSONObject();

        object.put("dummy_field", "dummy_value");
        object.put("data", new JSONObject());

        assertFalse(RequestFactory.getRequest(object).isPresent());
    }

    @Test
    public void testGetRequestInvalidType() {
        JSONObject object = new JSONObject("{\"request_type\":\"invalid\",\"data\":{}}");

        assertFalse(RequestFactory.getRequest(object).isPresent());
    }

    @Test
    public void testGetRequestNoData() {
        JSONObject object = new JSONObject();

        object.put("request_type", RequestType.LOGIN.name());

        assertFalse(RequestFactory.getRequest(object).isPresent());
    }

    @Test
    public void testGetRequestLoginRequest() {
        LoginRequest request = new LoginRequest("someuser", "somepass");

        Optional<Request> parsedRequest = RequestFactory.getRequest(request.toJSON());

        assertTrue(parsedRequest.isPresent());
        assertEquals(request, parsedRequest.get());
    }

    @Test
    public void testGetRequestLoginRequestInvalidData() {
        JSONObject object = new JSONObject("{\"request_type\":\"LOGIN\",\"data\":{\"username\": \"user\"}}");

        assertFalse(RequestFactory.getRequest(object).isPresent());
    }

    @Test
    public void testGetRequestSignUpRequest() {
        SignUpRequest request = new SignUpRequest(
                new User("jafar", "Jafar", "1234", "jafarabad", "Singer", "", Collections.emptySet())
        );

        Optional<Request> parsedRequest = RequestFactory.getRequest(request.toJSON());

        assertTrue(parsedRequest.isPresent());
        assertEquals(
                request,
                parsedRequest.get()
        );
    }

    @Test
    public void testGetRequestFileUploadRequest() {
        FileUploadRequest request = new FileUploadRequest(
                "amFmYXI=" // Decrypts to "jafar"
        );

        Optional<Request> parsedRequest = RequestFactory.getRequest(request.toJSON());

        assertTrue(parsedRequest.isPresent());
        assertEquals(
                request,
                parsedRequest.get()
        );
    }

    @Test
    public void testGetRequestFileUploadRequestInvalidData() {
        JSONObject invalidRequest = new JSONObject("{\"request_type\": \"FILE_UPLOAD\", \"data\": {}}");

        assertFalse(RequestFactory.getRequest(invalidRequest).isPresent());
    }

    @Test
    public void testGetRequestFileDownloadRequest() {
        FileDownloadRequest request = new FileDownloadRequest("testName");

        Optional<Request> parsedRequest = RequestFactory.getRequest(request.toJSON());

        assertTrue(parsedRequest.isPresent());
        assertEquals(
                request,
                parsedRequest.get()
        );
    }

    @Test
    public void testGetRequestFileDownloadRequestInvalidData() {
        JSONObject invalidRequest = new JSONObject("{\"request_type\": \"FILE_DOWNLOAD\", \"data\": {}}");

        assertFalse(RequestFactory.getRequest(invalidRequest).isPresent());
    }

    @Test
    public void testGetRequestUserTimelineRequest() {
        UserTimelineRequest request = new UserTimelineRequest("jafar", "1234", 1234, 2);

        Optional<Request> parsedRequest = RequestFactory.getRequest(request.toJSON());

        assertTrue(parsedRequest.isPresent());
        assertEquals(
                request,
                parsedRequest.get()
        );
    }

    @Test
    public void testGetRequestUserTimelineRequestInvalidData() {
        JSONObject invalidRequest = new JSONObject("{\"request_type\": \"USER_TIMELINE\", \"data\": {\"username\": \"jafar\", \"fromtime\": 12, \"count\": 2}}");

        assertFalse(RequestFactory.getRequest(invalidRequest).isPresent());
    }

    @Test
    public void testGetRequestUserInfoRequest() {
        UserInfoRequest request = new UserInfoRequest("jafar");
        Optional<Request> parsedRequest = RequestFactory.getRequest(request.toJSON());

        assertTrue(parsedRequest.isPresent());
        assertEquals(
                request,
                parsedRequest.get()
        );
    }

    @Test
    public void testGetRequestUserInfoRequestInvalidData() {
        JSONObject invalidRequest = new JSONObject("{\"request_type\":\"USER_INFO\",\"data\":{}}");

        assertFalse(RequestFactory.getRequest(invalidRequest).isPresent());
    }

    @Test
    public void testGetRequestAddCommentRequest() {
        AddCommentRequest request = new AddCommentRequest("jafar", "1234", 1, "Nice post.");
        Optional<Request> parsedRequest = RequestFactory.getRequest(request.toJSON());

        assertTrue(parsedRequest.isPresent());
        assertEquals(
                request,
                parsedRequest.get()
        );
    }

    @Test
    public void testGetRequestAddCommentRequestInvalidData() {
        JSONObject invalidRequest = new JSONObject("{\"request_type\":\"ADD_COMMENT\",\"data\":{\"postId\":1,\"text\":\"Nice post.\",\"username\":\"jafar\"}}");

        assertFalse(RequestFactory.getRequest(invalidRequest).isPresent());
    }

    @Test
    public void testGetRequestSetLikeRequest() {
        SetLikeRequest request = new SetLikeRequest("jafar", "1234", 1, true);
        Optional<Request> parsedRequest = RequestFactory.getRequest(request.toJSON());

        assertTrue(parsedRequest.isPresent());
        assertEquals(
                request,
                parsedRequest.get()
        );
    }

    @Test
    public void testGetRequestSetLikeRequestInvalidData() {
        JSONObject invalidRequest = new JSONObject("{\"request_type\":\"SET_LIKE\",\"data\":{\"password\":\"1234\",\"liked\":true,\"username\":\"jafar\"}}");

        assertFalse(RequestFactory.getRequest(invalidRequest).isPresent());
    }

    @Test
    public void testGetRequestUserPosts() {
        UserPostsRequest request = new UserPostsRequest("jafar");
        Optional<Request> parsedRequest = RequestFactory.getRequest(request.toJSON());

        assertTrue(parsedRequest.isPresent());
        assertEquals(
                request,
                parsedRequest.get()
        );
    }

    @Test
    public void testGetRequestInvalidData() {
        JSONObject invalidRequest = new JSONObject("{\"request_type\":\"USER_POSTS\",\"data\":{}}");

        assertFalse(RequestFactory.getRequest(invalidRequest).isPresent());
    }


}