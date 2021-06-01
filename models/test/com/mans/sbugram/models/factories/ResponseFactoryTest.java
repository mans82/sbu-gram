package com.mans.sbugram.models.factories;

import com.mans.sbugram.models.responses.LoginResponse;
import com.mans.sbugram.models.responses.Response;
import com.mans.sbugram.models.responses.ResponseType;
import com.mans.sbugram.models.responses.SignUpResponse;
import org.json.JSONObject;
import org.junit.Test;

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

}