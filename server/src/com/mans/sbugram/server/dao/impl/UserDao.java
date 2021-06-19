package com.mans.sbugram.server.dao.impl;

import com.mans.sbugram.models.User;
import com.mans.sbugram.server.dao.Dao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserDao implements Dao<User, String> {

    private final String dataDirectory;
    private final Map<String, User> cache = new HashMap<>();

    public UserDao(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    @Override
    public Optional<User> get(String id) throws PersistentOperationException {
        List<String> usernames = this.getDirectoryFiles(this.dataDirectory).stream()
                .map(pathString -> Paths.get(pathString).getFileName().toString())
                .map(filename -> filename.substring(0, filename.length() - 5))
                .collect(Collectors.toList());

        if (usernames.contains(id.toLowerCase())) {

            JSONObject object;
            try {
                object = new JSONObject(new JSONTokener(
                        this.getFileReader(Paths.get(this.dataDirectory, id.toLowerCase() + ".json").toString())
                ));
            } catch (IOException e) {
                throw new PersistentOperationException(e);
            }

            String username = object.getString("username");
            String name = object.getString("name");
            String password = object.getString("password");
            String city = object.getString("city");
            String bio = object.getString("bio");
            String profilePhotoFilename = object.getString("profilePhotoFilename");
            Set<String> followingUsersUsernames = object.getJSONArray("followingUsersUsernames").toList().stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet());

            return Optional.of(
                    new User(username, name, password, city, bio, profilePhotoFilename, followingUsersUsernames)
            );
        } else {
            return Optional.empty();
        }
    }

    private Optional<User> getOrEmpty(String id) {
        try {
            return this.get(id);
        } catch (PersistentOperationException e) {
            return Optional.empty();
        }
    }

    public List<User> getUsers(Predicate<User> predicate) {
        return this.getDirectoryFiles(this.dataDirectory).stream()
                .map(pathString -> Paths.get(pathString).getFileName().toString())
                .map(filename -> filename.substring(0, filename.length() - 5))
                .map(this::getOrEmpty)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(predicate)
                .collect(Collectors.toList());
    }

    @Override
    public void save(User data) throws PersistentOperationException{
        try {
            Writer fileWriter = this.getFileWriter(Paths.get(this.dataDirectory, data.username.toLowerCase() + ".json").toAbsolutePath().toString());
            data.toJSON().write(fileWriter);
            fileWriter.flush();
        } catch (IOException e) {
            throw new PersistentOperationException(e);
        }
    }


}
