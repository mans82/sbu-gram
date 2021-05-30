package com.mans.sbugram.server.models.factories;

import com.mans.sbugram.server.models.responses.LoginResponse;
import com.mans.sbugram.server.models.responses.Response;
import com.mans.sbugram.server.models.responses.ResponseType;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class ResponseFactoryTest {

    @Test
    public void testGetResponseLoginResponse() {
        LoginResponse testLoginResponse = new LoginResponse(true, "Logged in");
        Optional<Response> parsedResponse = ResponseFactory.getResponse(testLoginResponse.toJSON());

        assertTrue(parsedResponse.isPresent());
        assertEquals(testLoginResponse, parsedResponse.get());
    }

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

}