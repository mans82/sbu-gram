package com.mans.sbugram.models.factories;

import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.*;
import org.json.JSONObject;
import org.junit.Test;

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
                new User("jafar", "Jafar", "1234", "jafarabad", "Singer")
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
}