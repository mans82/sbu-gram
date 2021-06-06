package com.mans.sbugram.server.events;

import com.mans.sbugram.models.UploadedFile;
import com.mans.sbugram.models.requests.FileUploadRequest;
import com.mans.sbugram.models.requests.Request;
import com.mans.sbugram.models.requests.RequestType;
import com.mans.sbugram.models.responses.FileUploadResponse;
import com.mans.sbugram.models.responses.Response;
import com.mans.sbugram.server.dao.impl.UploadedFileDao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.server.exceptions.RequestTypeMismatchException;

import java.util.Date;
import java.util.Random;

public class FileUploadEventHandler implements EventHandler {

    private final UploadedFileDao dao;
    private final static long MAX_FILE_SIZE = 400 * 1024; // 400 KB
    final static int RANDOM_FILENAME_STRING_LENGTH = 6;
    final static char[] RANDOM_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    public FileUploadEventHandler(UploadedFileDao dao) {
        this.dao = dao;
    }

    @Override
    public Response handleEvent(Request request) throws RequestTypeMismatchException {
        if (!(request instanceof FileUploadRequest)) {
            throw new RequestTypeMismatchException(this.getRequestType().name());
        }

        String blob = ((FileUploadRequest) request).blob;

        if (blob.length() > (4f / 3f) * (MAX_FILE_SIZE)) {
            // File is too large
            return new FileUploadResponse(false, "File is too large", "");
        }

        // File is ok, save it
        UploadedFile uploadedFile = new UploadedFile(this.generateName(new Date(), new Random(), RANDOM_FILENAME_STRING_LENGTH), blob);
        try {
            dao.save(uploadedFile);
        } catch (PersistentOperationException e) {
            return new FileUploadResponse(false, "Server error", "");
        }

        return new FileUploadResponse(true, "", uploadedFile.name);
    }

    String randomString(int len, Random random) {
        return random.ints(len, 0, RANDOM_CHARS.length)
                .mapToObj(i -> RANDOM_CHARS[i])
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    String generateName(Date date, Random random, int randomLength) {
        return date.getTime() + "_" + this.randomString(randomLength, random);
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.FILE_UPLOAD;
    }
}
