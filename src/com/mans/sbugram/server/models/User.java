package com.mans.sbugram.server.models;

import com.mans.sbugram.server.models.interfaces.JSONRepresentable;
import com.sun.istack.internal.NotNull;
import org.json.JSONObject;

import java.util.Objects;

public class User implements JSONRepresentable {

    public final String username;
    public final String name;
    public final String password;

    public User(@NotNull String username, @NotNull String name, @NotNull String password) {
        this.username = username;
        this.name = name;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username) && name.equals(user.name) && password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, name, password);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("username", username);
        result.put("name", name);
        result.put("password", password);

        return result;
    }
}
