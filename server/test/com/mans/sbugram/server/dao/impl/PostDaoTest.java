package com.mans.sbugram.server.dao.impl;

import com.mans.sbugram.models.Comment;
import com.mans.sbugram.models.Post;
import com.mans.sbugram.server.exceptions.PersistentDataDoesNotExistException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class PostDaoTest {

    File tempDataDirectory;
    PostDao dao;

    @Before
    public void setUp() throws IOException {
        tempDataDirectory = Files.createTempDirectory("posts").toFile();
        dao = new PostDao(tempDataDirectory.getAbsolutePath());
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
        FileWriter writer = new FileWriter(Paths.get(tempDataDirectory.getAbsolutePath(), "1.json").toFile());

        Post testPost = new Post(1, 123, "title", "content", "", "jafar", Collections.emptySet(), Collections.emptySet());
        testPost.toJSON().write(writer).flush();

        Optional<Post> queriedPost = dao.get(1);

        assertTrue(queriedPost.isPresent());
        assertEquals(testPost, queriedPost.get());
    }

    @Test
    public void testGetNonExistentPost() throws Exception {
        FileWriter writer = new FileWriter(Paths.get(tempDataDirectory.getAbsolutePath(), "1.json").toFile());

        Post testPost = new Post(2, 123, "title", "content", "", "jafar", Collections.emptySet(), Collections.emptySet());
        testPost.toJSON().write(writer).flush();

        Optional<Post> post = dao.get(10);

        assertFalse(post.isPresent());
    }

    @Test
    public void testGetNoPost() throws Exception {
        assertFalse(dao.get(1234).isPresent());
    }

    @Test
    public void testSave() throws Exception {
        dao.save(
                new Post(3, 123, "title", "content", "", "jafar",
                        new HashSet<>(Arrays.asList(
                                new Comment("asghar", "Salam!"),
                                new Comment("akbar", "digar nafrestid.")
                        )), Collections.emptySet())
        );
        dao.save(
                new Post(300, 456, "title2", "content2", "", "another_jafar", Collections.emptySet(), Collections.emptySet())
        );

        JSONObject savedFileJSON1 = new JSONObject(new JSONTokener(new FileReader(
                        Paths.get(tempDataDirectory.getAbsolutePath(), "0.json").toString()
        )));

        assertEquals(0, savedFileJSON1.getInt("id"));
        assertEquals(123, savedFileJSON1.getLong("postedTime"));
        assertEquals("title", savedFileJSON1.getString("title"));
        assertEquals("content", savedFileJSON1.getString("content"));
        assertEquals("", savedFileJSON1.getString("photoFilename"));
        assertEquals("jafar", savedFileJSON1.getString("posterUsername"));
        assertFalse(savedFileJSON1.getBoolean("isRepost"));
        assertEquals(2, savedFileJSON1.getJSONArray("comments").length());
        assertTrue(IntStream.range(0, savedFileJSON1.getJSONArray("comments").length())
             .anyMatch(i -> savedFileJSON1.getJSONArray("comments").
                     getJSONObject(i).
                     getString("username")
                     .equals("asghar"))
        );
        assertTrue(IntStream.range(0, savedFileJSON1.getJSONArray("comments").length())
             .anyMatch(i -> savedFileJSON1.getJSONArray("comments").
                     getJSONObject(i).
                     getString("text")
                     .equals("digar nafrestid."))
        );

        JSONObject savedFileJSON2 = new JSONObject(new JSONTokener(new FileReader(
                Paths.get(tempDataDirectory.getAbsolutePath(), "1.json").toString()
        )));

        assertEquals(1, savedFileJSON2.getInt("id"));
    }

    @Test
    public void testSaveAndThenGet() throws Exception {
        dao.save(
                new Post(3, 123, "title", "content", "", "jafar", Collections.emptySet(), Collections.emptySet())
        );

        Optional<Post> retrievedPost = dao.get(0);

        assertTrue(retrievedPost.isPresent());
    }

    @Test
    public void testGetPosts() throws Exception {
        dao.save(
                new Post(1, 234, "title2", "content2", "", "jafar2", Collections.emptySet(), Collections.emptySet())
        );
        dao.save(
                new Post(1, 123, "title1", "content1", "", "jafar", Collections.emptySet(), Collections.emptySet())
        );
        dao.save(
                new Post(1, 345, "title3", "content3", "", "jafar3", Collections.emptySet(), Collections.emptySet())
        );

        List<Post> filteredPost = dao.getPosts(post -> post.postedTime < 300, 1000);

        assertEquals(2, filteredPost.size());
        assertEquals("title2", filteredPost.get(0).title);
        assertEquals("title1", filteredPost.get(1).title);

        List<Post> filteredPostEmpty = dao.getPosts(post -> post.title.equals("non existent title"), 1000);
        assertTrue(filteredPostEmpty.isEmpty());
    }

    @Test
    public void testUpdate() throws Exception {
        dao.save(
                new Post(1, 123, "title1", "content1", "", "jafar", Collections.emptySet(), Collections.emptySet())
        );

        Post newPost = new Post(
                0, 333, "new title", "new content", "", "jafar", Collections.emptySet(),
                Collections.emptySet());

        Post newPostWithDummyId = new Post(
                3, 333, "new title", "new content", "", "jafar", Collections.emptySet(),
                Collections.emptySet());

        dao.update(0, newPostWithDummyId);

        Optional<Post> retrievedPost = dao.get(0);

        assertTrue(retrievedPost.isPresent());
        assertEquals(newPost, retrievedPost.get());
    }

    @Test(expected = PersistentDataDoesNotExistException.class)
    public void testUpdateNonExistentPost() throws Exception {
        dao.update(1000, new Post(1, 123, "title1", "content1", "", "jafar", Collections.emptySet(), Collections.emptySet()));
    }
}