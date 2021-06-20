package com.mans.sbugram.server.dao.impl;

import com.mans.sbugram.models.Post;
import com.mans.sbugram.server.dao.Dao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PostDao implements Dao<Post, Integer> {

    private final String filesDirectory;

    public PostDao(String filesDirectory) {
        this.filesDirectory = filesDirectory;
    }

    @Override
    public Optional<Post> get(Integer id) throws PersistentOperationException {
        List<String> postFiles = this.getDirectoryFiles(filesDirectory);

        Optional<String> queriedPostFilename = postFiles.stream()
                .filter(filename -> Paths.get(filename).getFileName().toString().equals(id + ".json"))
                .findFirst();

        if (!queriedPostFilename.isPresent()) {
            return Optional.empty();
        }

        JSONObject postJSONObject;
        int postId;
        long postedTime;
        String title;
        String content;
        String photoFilename;
        String posterUsername;
        boolean isRepost;
        int repostedPostId;

        try {
            postJSONObject = new JSONObject(new JSONTokener(this.getFileReader(queriedPostFilename.get())));
        } catch (FileNotFoundException e) {
            throw new PersistentOperationException(e);
        }

        postId = postJSONObject.getInt("id");
        postedTime = postJSONObject.getLong("postedTime");
        title = postJSONObject.getString("title");
        content = postJSONObject.getString("content");
        photoFilename = postJSONObject.getString("photoFilename");
        posterUsername = postJSONObject.getString("posterUsername");
        isRepost = postJSONObject.getBoolean("isRepost");
        repostedPostId = postJSONObject.getInt("repostedPostId");


        return Optional.of(
                new Post(postId, postedTime, title, content, photoFilename, posterUsername, isRepost, repostedPostId)
        );

    }

    @Override
    public void save(Post data) throws PersistentOperationException {
        OptionalInt optionalMaxPostId = this.getDirectoryFiles(this.filesDirectory).stream()
                .map(path -> Paths.get(path).getFileName().toString())
                .map(filename -> filename.substring(0, filename.length() - 5)) // remove .json at the end of file name
                .mapToInt(Integer::valueOf)
                .max();

        int newPostId = optionalMaxPostId.isPresent() ? optionalMaxPostId.getAsInt() + 1 : 0;

        try {
            Writer fileWriter = this.getFileWriter(Paths.get(this.filesDirectory, newPostId + ".json").toString());
            data.toJSON().put("id", newPostId).write(fileWriter);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            throw new PersistentOperationException(e);
        }
    }

    private Optional<Post> getOrEmpty(Integer id) {
        try {
            return this.get(id);
        } catch (PersistentOperationException e) {
            return Optional.empty();
        }
    }

    public List<Post> getPosts(Predicate<Post> predicate, int limit) {
        return this.getDirectoryFiles(this.filesDirectory).stream()
                .map(filename -> Paths.get(filename).getFileName().toString())
                .map(filename -> filename.substring(0, filename.length() - 5))
                .map(postIdString -> this.getOrEmpty(Integer.valueOf(postIdString)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(predicate)
                .sorted((post1, post2) -> (int)(post1.postedTime - post2.postedTime))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
