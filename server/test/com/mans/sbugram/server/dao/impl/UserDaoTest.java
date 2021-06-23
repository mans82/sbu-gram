package com.mans.sbugram.server.dao.impl;

import com.mans.sbugram.models.User;
import com.mans.sbugram.server.exceptions.PersistentDataDoesNotExistException;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserDaoTest {

    File tempDataDirectory;
    UserDao dao;
    final User testUser = new User("_jafar_", "Jafar", "1234", "jafarabad", "Singer", "", new HashSet<>());

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

    @Test
    public void testGetUser() throws Exception {
        dao.save(
                new User("user_1", "User1", "1234", "", "", "", Arrays.stream(new String[] {"user_2"}).collect(Collectors.toSet()))
        );
        dao.save(
                new User("user_2", "User2", "123456", "", "", "", Collections.emptySet())
        );
        dao.save(
                new User("user_3", "User3", "1234", "", "", "", Arrays.stream(new String[] {"user_2"}).collect(Collectors.toSet()))
        );

        List<User> user2Followers = dao.getUsers(user -> user.followingUsersUsernames.contains("user_2"));

        assertEquals(2, user2Followers.size());
        assertTrue(
                user2Followers.stream()
                .anyMatch(user -> user.username.equals("user_1"))
        );
        assertTrue(
                user2Followers.stream()
                .anyMatch(user -> user.username.equals("user_3"))
        );
    }

    @Test
    public void testUpdate() throws Exception {
        User newTestUserWithDummyUser = new User("dummyUser", "J Jafar", "09876", "", "", "", Collections.emptySet());
        User newTestUser = new User(testUser.username, "J Jafar", "09876", "", "", "", Collections.emptySet());
        dao.save(testUser);
        dao.update(testUser.username, newTestUserWithDummyUser);

        Optional<User> retrievedUser = dao.get(testUser.username);

        assertTrue(retrievedUser.isPresent());
        assertEquals(newTestUser, retrievedUser.get());
    }

    @Test(expected = PersistentDataDoesNotExistException.class)
    public void testUpdateNonExistentUser() throws Exception{
        dao.update("nonExistantUser", testUser);
    }
}
