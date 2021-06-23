package com.mans.sbugram.models;

import com.mans.sbugram.models.interfaces.JSONRepresentable;
import org.json.JSONObject;

public class Comment implements JSONRepresentable {

    public final String username;
    public final String text;

    public Comment(String username, String text) {
        this.username = username;
        this.text = text;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("username", username);
        result.put("text", text);

        return result;
    }
}
