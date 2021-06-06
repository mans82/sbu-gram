package com.mans.sbugram.server.events;

import com.mans.sbugram.models.UploadedFile;
import com.mans.sbugram.models.requests.FileUploadRequest;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.responses.FileUploadResponse;
import com.mans.sbugram.server.dao.impl.UploadedFileDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FileUploadEventHandlerTest {

    UploadedFileDao mockDao;
    FileUploadEventHandler handler;

    @Before
    public void setUp() {
        mockDao = Mockito.mock(UploadedFileDao.class);
        handler = new FileUploadEventHandler(mockDao);
    }

    @Test
    public void testRequestType() {
        assertEquals(RequestType.FILE_UPLOAD, handler.getRequestType());
    }

    @Test
    public void testFileUploadProperFile() throws Exception {
        String testBlob = "amFmYXJibG9i";

        FileUploadResponse receivedResponse = (FileUploadResponse) handler.handleEvent(
                new FileUploadRequest(testBlob)
        );

        assertTrue(receivedResponse.successful);
        assertFalse(receivedResponse.filename.isEmpty());

        verify(mockDao, times(1)).save(any(UploadedFile.class));
    }

    @Test
    public void testFileUploadLargeFile() throws Exception {
        StringBuilder largeBlobBuilder = new StringBuilder();
        for (int i = 0; i < 1024 * 1024; i++) {
            largeBlobBuilder.append("H");
        }
        String largeBlob = largeBlobBuilder.toString();

        FileUploadResponse receivedResponse = (FileUploadResponse) handler.handleEvent(
                new FileUploadRequest(largeBlob)
        );

        assertFalse(receivedResponse.successful);

        verify(mockDao, times(0)).save(any(UploadedFile.class));
    }

    @Test
    public void testRandomString() {
        Random mockRandom = mock(Random.class);
        when(mockRandom.ints(5, 0, FileUploadEventHandler.RANDOM_CHARS.length))
                .thenReturn(IntStream.range(0, 5));

        assertEquals("ABCDE", handler.randomString(5, mockRandom));
    }

    @Test
    public void testGenerateName() {
        Random mockRandom = mock(Random.class);
        when(mockRandom.ints(5, 0, FileUploadEventHandler.RANDOM_CHARS.length))
                .thenReturn(IntStream.range(0, 5));

        Date mockDate = mock(Date.class);
        when(mockDate.getTime())
                .thenReturn(1234L);

        assertEquals("1234_ABCDE", handler.generateName(mockDate, mockRandom, 5));
    }

    @Test
    public void testFileUploadPersistentException() throws Exception {
        doThrow(PersistentOperationException.class)
                .when(mockDao).save(any(UploadedFile.class));

        String testBlob = "amFmYXJibG9i";

        FileUploadResponse receivedResponse = (FileUploadResponse) handler.handleEvent(
                new FileUploadRequest(testBlob)
        );

        assertFalse(receivedResponse.successful);
    }

    @Test
    public void testRequestMismatch() {
        Request badRequest = new Request() {
            @Override
            public RequestType getRequestType() {
                return RequestType.SIGNUP;
            }

            @Override
            public JSONObject toJSON() {
                return null;
            }
        };

        assertThrows(RequestTypeMismatchException.class, () -> handler.handleEvent(badRequest));
    }
}