package com.mans.sbugram.server.dao.impl;

import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.models.User;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class UserDaoTest {

    File tempDataDirectory;
    UserDao dao;
    final User testUser = new User("_jafar_", "Jafar", "1234", "jafarabad", "Singer", "");

    @Before
    public void setUp() throws IOException {
        tempDataDirectory = Files.createTempDirectory("users").toFile();
        dao = new UserDao(tempDataDirectory.getAbsolutePath());
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
    public void testGet() throws IOException, PersistentOperationException {

        // Write user info file manually
        FileWriter writer = new FileWriter(Paths.get(tempDataDirectory.getAbsolutePath(), testUser.username) + ".json");

        testUser.toJSON().write(writer);
        writer.flush();

        assertEquals(
                Optional.of(testUser),
                dao.get("_jafar_")
        );

        assertEquals(
                Optional.empty(),
                dao.get("steve")
        );
    }

    @Test
    public void testGetNoFiles() throws PersistentOperationException {

        assertEquals(
                Optional.empty(),
                dao.get("somerandomusername")
        );

    }

    @Test
    public void testSave() throws PersistentOperationException, FileNotFoundException {

        dao.save(testUser);

        JSONObject object = new JSONObject(new JSONTokener(
                new FileReader(
                        Paths.get(this.tempDataDirectory.getAbsolutePath(), testUser.username + ".json")
                                .toFile()
                                .getAbsolutePath()
                )));

        assertEquals(testUser.username, object.getString("username"));
        assertEquals(testUser.name, object.getString("name"));
        assertEquals(testUser.password, object.getString("password"));

    }

    @Test
    public void testSaveAndThenGet() throws PersistentOperationException {

        dao.save(testUser);

        assertEquals(
                Optional.of(testUser),
                dao.get(testUser.username)
        );

        assertEquals(
                Optional.empty(),
                dao.get("nonexistent_username")
        );

    }
}
