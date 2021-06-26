package com.mans.sbugram.server.dao.impl;

import com.mans.sbugram.models.Comment;
import com.mans.sbugram.models.Post;
import com.mans.sbugram.server.dao.Dao;
import com.mans.sbugram.server.exceptions.PersistentDataDoesNotExistException;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        String originalPosterUsername;
        Set<Comment> comments;
        Set<String> likedUsersUsernames;

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
        originalPosterUsername = postJSONObject.getString("originalPosterUsername");

        JSONArray commentsJSONArray = postJSONObject.getJSONArray("comments");
        comments = IntStream.range(0, commentsJSONArray.length())
                .mapToObj(commentsJSONArray::getJSONObject)
                .map(commentJSON -> new Comment(commentJSON.getString("username"), commentJSON.getString("text")))
                .collect(Collectors.toSet());

        JSONArray likedUsersJSONArray = postJSONObject.getJSONArray("likedUsersUsernames");
        likedUsersUsernames = IntStream.range(0, likedUsersJSONArray.length())
                .mapToObj(likedUsersJSONArray::getString)
                .collect(Collectors.toSet());


        return Optional.of(
                new Post(postId, postedTime, title, content, photoFilename, posterUsername, comments, isRepost, originalPosterUsername, likedUsersUsernames)
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

    @Override
    public void update(Integer id, Post newData) throws PersistentOperationException, PersistentDataDoesNotExistException {
        if (this.get(id).isPresent()) {
            Post newPost = new Post(
                    id,
                    newData.postedTime,
                    newData.title,
                    newData.content,
                    newData.photoFilename,
                    newData.posterUsername,
                    newData.comments,
                    newData.isRepost,
                    newData.originalPosterUsername,
                    newData.likedUsersUsernames);

            try {
                Writer fileWriter = this.getFileWriter(Paths.get(this.filesDirectory, id + ".json").toString());
                newPost.toJSON().write(fileWriter);
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                throw new PersistentOperationException(e);
            }
        } else {
            throw new PersistentDataDoesNotExistException(String.valueOf(id));
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
                .sorted((post1, post2) -> (int)(post2.postedTime - post1.postedTime))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
