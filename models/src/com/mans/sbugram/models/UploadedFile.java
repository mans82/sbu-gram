package com.mans.sbugram.models;

import com.mans.sbugram.models.interfaces.JSONRepresentable;
import org.json.JSONObject;

import java.util.Objects;

public class UploadedFile implements JSONRepresentable {

    public final String name;
    public final String blob;

    public UploadedFile(String name, String blob) {
        this.name = name;
        this.blob = blob;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("name", name);
        result.put("blob", blob);

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UploadedFile that = (UploadedFile) o;
        return name.equals(that.name) && blob.equals(that.blob);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, blob);
    }
}
