package com.mans.sbugram.server.events;

import com.mans.sbugram.server.dao.impl.UserDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;
import com.mans.sbugram.models.User;
import com.mans.sbugram.models.requests.LoginRequest;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.requests.SignUpRequest;
import com.mans.sbugram.models.responses.SignUpResponse;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SignUpEventHandlerTest {

    private UserDao mockDao;
    private SignUpEventHandler eventHandler;
    private final User testUser = new User("jafar", "Jafar", "12345678", "jafarabad", "Singer", "");


    @Before
    public void setUp() {
        mockDao = mock(UserDao.class);
        eventHandler = new SignUpEventHandler(mockDao);
    }

    @Test
    public void testRequestType() {
        assertEquals(RequestType.SIGNUP, new SignUpEventHandler(mockDao).getRequestType());
    }

    @Test
    public void testSignUpNewUsername() throws Exception {
        when(mockDao.get("jafar"))
                .thenReturn(Optional.empty());

        SignUpResponse response = (SignUpResponse) eventHandler.handleEvent(
                new SignUpRequest(this.testUser)
        );

        assertTrue(response.successful);
    }

    @Test
    public void testRequestTypeMismatch() {
        SignUpEventHandler handler = new SignUpEventHandler(mockDao);

        assertThrows(RequestTypeMismatchException.class, () -> handler.handleEvent(new LoginRequest("dummyuser", "dummypass")));
    }

    @Test
    public void testSignUpUsernameInvalidLength() throws Exception {
        when(mockDao.get("jafar"))
                .thenReturn(Optional.of(testUser));

        User userWithInvalidUsername = new User("usr", "User", "12345678", "", "", "");
        SignUpResponse response = (SignUpResponse) eventHandler.handleEvent(
                new SignUpRequest(userWithInvalidUsername)
        );

        assertFalse(response.successful);
    }

    @Test
    public void testSignUpUsernameAlreadySignedUp() throws Exception {
        when(mockDao.get("jafar"))
                .thenReturn(Optional.of(testUser));

        User otherUserWithSameUsername = new User("jafar", "Asghar", "432101234", "AsgharAbad", "Not a singer", "");
        SignUpResponse response = (SignUpResponse) eventHandler.handleEvent(
                new SignUpRequest(otherUserWithSameUsername)
        );

        assertFalse(response.successful);
    }

    @Test
    public void testSignUpInvalidPasswordNotEnoughChars() throws Exception {
        when(mockDao.get("jafar"))
                .thenReturn(Optional.of(testUser));

        User userWithInvalidPassword = new User("jafar", "Jafar", "hehe", "JafarAbad", "Singer", "");
        SignUpResponse response = (SignUpResponse) eventHandler.handleEvent(
                new SignUpRequest(userWithInvalidPassword)
        );

        assertFalse(response.successful);
    }

    @Test
    public void testSignUpInvalidPasswordInvalidChars() throws Exception {
        when(mockDao.get("jafar"))
                .thenReturn(Optional.of(testUser));

        User userWithInvalidPassword = new User("jafar", "Jafar", "1nv4l1D_cH4r5", "JafarAbad", "Singer", "");
        SignUpResponse response = (SignUpResponse) eventHandler.handleEvent(
                new SignUpRequest(userWithInvalidPassword)
        );

        assertFalse(response.successful);
    }

    @Test
    public void testPersistentException() throws Exception {
        when(mockDao.get("jafar"))
                .thenThrow(new PersistentOperationException(new Exception()));

        SignUpResponse response = (SignUpResponse) eventHandler.handleEvent(
                new SignUpRequest(testUser)
        );

        assertFalse(response.successful);
    }

}