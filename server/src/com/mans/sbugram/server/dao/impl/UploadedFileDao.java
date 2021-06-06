package com.mans.sbugram.server.dao.impl;

import com.mans.sbugram.models.UploadedFile;
import com.mans.sbugram.server.dao.Dao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class UploadedFileDao implements Dao<UploadedFile, String> {

    private final String filesDirectory;
    private static final Charset FILE_CHARSET = StandardCharsets.US_ASCII;

    public UploadedFileDao(String filesDirectory) {
        this.filesDirectory = filesDirectory;
    }

    @Override
    public Optional<UploadedFile> get(String id) throws PersistentOperationException {

        File fileObject = Paths.get(this.filesDirectory, id).toFile();

        if (!fileObject.exists()) {
            return Optional.empty();
        }

        String blob;
        try {
            blob = new String(Files.readAllBytes(fileObject.toPath()), UploadedFileDao.FILE_CHARSET);
        } catch (IOException e) {
            throw new PersistentOperationException(e);
        }

        return Optional.of(new UploadedFile(id, blob));
    }

    @Override
    public void save(UploadedFile data) throws PersistentOperationException {
        try (Writer fileWriter = this.getFileWriter(Paths.get(this.filesDirectory, data.name).toString())) {
            fileWriter.write(data.blob);
            fileWriter.flush();
        } catch (IOException e) {
            throw new PersistentOperationException(e);
        }
    }
}
