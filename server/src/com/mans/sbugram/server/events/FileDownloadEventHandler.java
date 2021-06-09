package com.mans.sbugram.server.events;

import com.mans.sbugram.models.UploadedFile;
import com.mans.sbugram.models.requests.FileDownloadRequest;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.responses.FileDownloadResponse;
import com.mans.sbugram.models.responses.Response;
import com.mans.sbugram.server.dao.impl.UploadedFileDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;

import java.util.Optional;

public class FileDownloadEventHandler implements EventHandler {

    private final UploadedFileDao dao;

    public FileDownloadEventHandler(UploadedFileDao dao) {
        this.dao = dao;
    }

    @Override
    public Response handleEvent(Request request) throws RequestTypeMismatchException {
        if (!(request instanceof FileDownloadRequest)) {
            throw new RequestTypeMismatchException(this.getRequestType().name());
        }

        FileDownloadRequest fileDownloadRequest = (FileDownloadRequest) request;
        Optional<UploadedFile> uploadedFile;
        try {
            uploadedFile = dao.get(fileDownloadRequest.filename);
        } catch (PersistentOperationException e) {
            return new FileDownloadResponse(false, "Server error", "");
        }

        return uploadedFile.map(
                file -> new FileDownloadResponse(true, "", file.blob)
        ).orElseGet(
                () -> new FileDownloadResponse(false, "File not found", "")
        );
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.FILE_DOWNLOAD;
    }
}
