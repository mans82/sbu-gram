package com.mans.sbugram.server.events;

import com.mans.sbugram.server.exceptions.HandlerAlreadyExistsException;
import com.mans.sbugram.server.exceptions.UnhandledRequestTypeException;
import com.mans.sbugram.server.requests.Request;
import com.mans.sbugram.server.requests.RequestType;
import com.mans.sbugram.server.responses.Response;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertThrows;


class SampleLoginEventHandler implements EventHandler {

    public int dummyValue = 0;

    @Override
    public Response handleEvent(Request request) {
        dummyValue++;
        return null;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.LOGIN;
    }
}

class SampleSignUpEventHandler implements EventHandler {

    public int dummyValue = 0;

    @Override
    public Response handleEvent(Request request) {
        dummyValue++;
        return null;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.SIGNUP;
    }
}


public class EventManagerTest {

    private final Request sampleLoginRequest = new Request() {
        @Override
        public RequestType getRequestType() {
            return RequestType.LOGIN;
        }
    };
    private final Request sampleSignUpRequest = new Request() {
        @Override
        public RequestType getRequestType() {
            return RequestType.SIGNUP;
        }
    };

    @Test
    public void testProperEventManager() {
        EventManager manager = new EventManager();
        SampleLoginEventHandler loginEventHandler = new SampleLoginEventHandler();
        SampleSignUpEventHandler signUpEventHandler = new SampleSignUpEventHandler();

        try {
            manager.addEventHandler(loginEventHandler);
            manager.addEventHandler(signUpEventHandler);
        } catch (Exception e) {
            fail();
        }

        try {
            manager.handleRequest(sampleLoginRequest);
            manager.handleRequest(sampleLoginRequest);
            manager.handleRequest(sampleSignUpRequest);
        } catch (Exception e) {
            fail();
        }

        assertEquals(2, loginEventHandler.dummyValue);
        assertEquals(1, signUpEventHandler.dummyValue);
    }

    @Test
    public void testUnhandledEvent() {
        EventManager manager = new EventManager();
        SampleLoginEventHandler loginEventHandler = new SampleLoginEventHandler();

        try {
            manager.addEventHandler(loginEventHandler);
        } catch (HandlerAlreadyExistsException e) {
            fail();
        }

        assertThrows(UnhandledRequestTypeException.class, () -> manager.handleRequest(sampleSignUpRequest));
    }

    @Test
    public void testAddingDuplicateEventHandler() {
        EventManager manager = new EventManager();
        SampleLoginEventHandler loginEventHandler = new SampleLoginEventHandler();
        SampleLoginEventHandler duplicateLoginEventHandler = new SampleLoginEventHandler();

        try {
            manager.addEventHandler(loginEventHandler);
        } catch (HandlerAlreadyExistsException e) {
            fail();
        }

        assertThrows(HandlerAlreadyExistsException.class, () -> manager.addEventHandler(duplicateLoginEventHandler));
    }
}
