package com.mans.sbugram.server.models.factories;

import com.mans.sbugram.server.models.requests.LoginRequest;
import com.mans.sbugram.server.models.requests.Request;
import com.mans.sbugram.server.models.requests.RequestType;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

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
    public void testGetRequestInvalidData() {
        JSONObject object = new JSONObject("{\"request_type\":\"LOGIN\",\"data\":{\"username\": \"user\"}}");

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
}