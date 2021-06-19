package com.mans.sbugram.models;

import com.mans.sbugram.models.interfaces.JSONRepresentable;
import org.json.JSONObject;

import java.util.Objects;

public class Post implements JSONRepresentable {

    public final int id;
    public final long postedTime;
    public final String title;
    public final String content;
    public final String photoFilename;
    public final String posterUsername;
    public final boolean isRepost;
    public final int repostedPostId;

    public Post(int id, long postedTime, String title, String content, String photoFilename, String posterUsername, boolean isRepost, int repostedPostId) {
        this.id = id;
        this.postedTime = postedTime;
        this.title = title;
        this.content = content;
        this.photoFilename = photoFilename;
        this.posterUsername = posterUsername;
        this.isRepost = isRepost;
        this.repostedPostId = repostedPostId;
    }

    public Post(int id, long postedTime, String title, String content, String photoFilename, String posterUsername) {
        this(id, postedTime, title, content, photoFilename, posterUsername, false, -1);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();

        result.put("id", id);
        result.put("postedTime", postedTime);
        result.put("title", title);
        result.put("content", content);
        result.put("photoFilename", photoFilename);
        result.put("posterUsername", posterUsername);
        result.put("isRepost", isRepost);
        result.put("repostedPostId", repostedPostId);

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id && isRepost == post.isRepost && repostedPostId == post.repostedPostId && title.equals(post.title) && content.equals(post.content) && photoFilename.equals(post.photoFilename) && posterUsername.equals(post.posterUsername);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, photoFilename, posterUsername, isRepost, repostedPostId);
    }
}
