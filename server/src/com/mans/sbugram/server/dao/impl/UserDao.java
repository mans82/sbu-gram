package com.mans.sbugram.server.dao.impl;

import com.mans.sbugram.server.dao.Dao;
import com.mans.sbugram.server.exceptions.PersistentOperationException;
import com.mans.sbugram.models.User;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.*;

public class UserDao implements Dao<User, String> {

    private final String dataDirectory;
    private final Map<String, User> cache = new HashMap<>();

    public UserDao(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    private void updateCache() throws IOException, JSONException {

        List<String> userFilesPaths = this.getDirectoryFiles(this.dataDirectory);

        this.cache.clear();

        for (String path : userFilesPaths) {
            JSONObject object = new JSONObject(new JSONTokener(this.getFileReader(path)));

            String username = object.getString("username");
            String name = object.getString("name");
            String password = object.getString("password");
            String city = object.getString("city");
            String bio = object.getString("bio");

            this.cache.put(username, new User(username, name, password, city, bio));
        }
    }

    @Override
    public Optional<User> get(String id) throws PersistentOperationException {
        try {
            this.updateCache();
        } catch (IOException e) {
            throw new PersistentOperationException(e);
        }

        return Optional.ofNullable(this.cache.getOrDefault(id, null));
    }

    @Override
    public void save(User data) throws PersistentOperationException{
        try {
            Writer fileWriter = this.getFileWriter(Paths.get(this.dataDirectory, data.username + ".json").toAbsolutePath().toString());
            data.toJSON().write(fileWriter);
            fileWriter.flush();
        } catch (IOException e) {
            throw new PersistentOperationException(e);
        }
    }


}
