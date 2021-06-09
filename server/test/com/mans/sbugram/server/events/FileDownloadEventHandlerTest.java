package com.mans.sbugram.server.events;

import com.mans.sbugram.models.UploadedFile;
import com.mans.sbugram.models.requests.FileDownloadRequest;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.responses.FileDownloadResponse;
import com.mans.sbugram.server.dao.impl.UploadedFileDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileDownloadEventHandlerTest {

    UploadedFileDao mockDao;
    FileDownloadEventHandler handler;

    @Before
    public void setUp() {
        mockDao = mock(UploadedFileDao.class);
        handler = new FileDownloadEventHandler(mockDao);
    }

    @Test
    public void testRequestType() {
        assertEquals(RequestType.FILE_DOWNLOAD, new FileDownloadEventHandler(mockDao).getRequestType());
    }

    @Test
    public void testFileDownloadProperFile() throws Exception {
        UploadedFile expectedUploadedFile = new UploadedFile("testFile", "amFmYXI=");

        when(mockDao.get("testFile"))
                .thenReturn(Optional.of(expectedUploadedFile));

        FileDownloadResponse receivedResponse = (FileDownloadResponse) handler.handleEvent(
                new FileDownloadRequest("testFile")
        );

        assertTrue(receivedResponse.successful);
        assertEquals(expectedUploadedFile.blob, receivedResponse.blob);
    }

    @Test
    public void testFileDownloadNonExistent() throws Exception {
        when(mockDao.get("nonExistent"))
                .thenReturn(Optional.empty());

        FileDownloadResponse receivedResponse = (FileDownloadResponse) handler.handleEvent(
                new FileDownloadRequest("nonExistent")
        );

        assertFalse(receivedResponse.successful);
    }

    @Test
    public void testFileDownloadPersistentException() throws Exception {
        when(mockDao.get("badFile"))
                .thenThrow(PersistentOperationException.class);

        FileDownloadResponse receivedResponse = (FileDownloadResponse) handler.handleEvent(
                new FileDownloadRequest("badFile")
        );

        assertFalse(receivedResponse.successful);
    }

    @Test(expected = RequestTypeMismatchException.class)
    public void testRequestMismatch() throws Exception{
        handler.handleEvent(new Request() {
            @Override
            public RequestType getRequestType() {
                return RequestType.LOGIN;
            }

            @Override
            public JSONObject toJSON() {
                return null;
            }
        });
    }

}