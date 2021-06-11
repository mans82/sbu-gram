package com.mans.sbugram.models;

import com.mans.sbugram.models.interfaces.JSONRepresentable;
import org.json.JSONObject;

import java.util.Objects;

public class User implements JSONRepresentable {

    public final String username;
    public final String name;
    public final String password;
    public final String city;
    public final String bio;
    public final String profilePhotoFilename;

    public User(String username, String name, String password, String city, String bio, String profilePhotoFilename) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.city = city;
        this.bio = bio;
        this.profilePhotoFilename = profilePhotoFilename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username) && name.equals(user.name) && password.equals(user.password) && city.equals(user.city) && bio.equals(user.bio) && profilePhotoFilename.equals(user.profilePhotoFilename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, name, password, city, bio, profilePhotoFilename);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("username", username);
        result.put("name", name);
        result.put("password", password);
        result.put("city", city);
        result.put("bio", bio);
        result.put("profilePhotoFilename", profilePhotoFilename);

        return result;
    }
}
