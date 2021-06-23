package com.mans.sbugram.server.dao.impl;

import com.mans.sbugram.models.UploadedFile;
import com.mans.sbugram.server.exceptions.PersistentDataDoesNotExistException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import static org.junit.Assert.*;

public class UploadedFileDaoTest {

    File tempDataDirectory;
    UploadedFileDao dao;
    final String testBase64Blob = "amFmYXI="; // Decrypts to "jafar"

    @Before
    public void setUp() throws IOException {
        tempDataDirectory = Files.createTempDirectory("uploaded").toFile();
        dao = new UploadedFileDao(tempDataDirectory.getAbsolutePath());
    }

    @After
    public void tearDown() {
        File[] tempFiles = tempDataDirectory.listFiles();

        if (tempFiles != null) {
            for (File file : tempFiles) {
                file.delete();
            }
        }

        tempDataDirectory.delete();
    }

    @Test
    public void testGet() throws Exception {
        Files.write(
                Paths.get(this.tempDataDirectory.getAbsolutePath(), "testFile"),
                testBase64Blob.getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE
        );

        Optional<UploadedFile> result = dao.get("testFile");

        assertTrue(result.isPresent());
        assertEquals(
                new UploadedFile("testFile", testBase64Blob),
                result.get()
        );
    }

    @Test
    public void testGetNoFiles() throws Exception {
        assertFalse(dao.get("randomFileName").isPresent());
    }

    @Test
    public void testSave() throws Exception {
        UploadedFile fileToSave = new UploadedFile("testFile", testBase64Blob);
        dao.save(fileToSave);

        String actualBlob = new String(
                Files.readAllBytes(Paths.get(this.tempDataDirectory.toString(), "testFile")),
                StandardCharsets.US_ASCII
        );

        assertEquals(
                testBase64Blob,
                actualBlob
        );
    }

    @Test
    public void testSaveAndThenGet() throws Exception {
        UploadedFile testFile = new UploadedFile("testFile", testBase64Blob);
        dao.save(testFile);

        Optional<UploadedFile> retrievedFile = dao.get("testFile");

        assertTrue(retrievedFile.isPresent());
        assertEquals(testFile, retrievedFile.get());
    }

    @Test
    public void testUpdate() throws Exception {
        dao.save(new UploadedFile("data", testBase64Blob));
        dao.update("data", new UploadedFile("dummyId", "new data"));

        Optional<UploadedFile> retrievedFile = dao.get("data");

        assertTrue(retrievedFile.isPresent());
        assertEquals("new data", retrievedFile.get().blob);
    }

    @Test(expected = PersistentDataDoesNotExistException.class)
    public void testUpdateNonExistentFile() throws Exception {
        dao.update("nonExistentFile", new UploadedFile("dummyId", testBase64Blob));
    }
}