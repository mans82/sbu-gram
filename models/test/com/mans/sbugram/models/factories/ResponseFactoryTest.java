package com.mans.sbugram.models.factories;

import com.mans.sbugram.models.responses.*;
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
        JSONObject invalidRequest = new JSONObject("{\"response_type\": \"FILE_UPLOAD\", \"successful\": true, \"message\": \"\", \"data\": {}}");
        Optional<Response> parsedResponse = ResponseFactory.getResponse(invalidRequest);

        assertFalse(parsedResponse.isPresent());
    }

    @Test
    public void testGetResponseFileUploadResponseNoData() {
        JSONObject invalidRequest = new JSONObject("{\"response_type\": \"FILE_UPLOAD\", \"successful\": true, \"message\": \"\"}");
        Optional<Response> parsedResponse = ResponseFactory.getResponse(invalidRequest);

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
        JSONObject invalidRequest = new JSONObject("{\"response_type\": \"FILE_DOWNLOAD\", \"successful\": true, \"message\": \"\", \"data\": {}}");
        Optional<Response> parsedResponse = ResponseFactory.getResponse(invalidRequest);

        assertFalse(parsedResponse.isPresent());
    }

    @Test
    public void testGetResponseFileDownloadResponseNoData() {
        JSONObject invalidRequest = new JSONObject("{\"response_type\": \"FILE_DOWNLOAD\", \"successful\": true, \"message\": \"\"}");
        Optional<Response> parsedResponse = ResponseFactory.getResponse(invalidRequest);

        assertFalse(parsedResponse.isPresent());
    }

}