package com.mans.sbugram.models;

import com.mans.sbugram.models.interfaces.JSONRepresentable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Post implements JSONRepresentable {

    public final int id;
    public final long postedTime;
    public final String title;
    public final String content;
    public final String photoFilename;
    public final String posterUsername;
    public final Set<Comment> comments;
    public final boolean isRepost;
    public final String originalPosterUsername;
    public final Set<String> likedUsersUsernames;

    public Post(int id, long postedTime, String title, String content, String photoFilename, String posterUsername, Set<Comment> comments, boolean isRepost, String originalPosterUsername, Set<String> likedUsersUsernames) {
        this.id = id;
        this.postedTime = postedTime;
        this.title = title;
        this.content = content;
        this.photoFilename = photoFilename;
        this.posterUsername = posterUsername;
        this.comments = Collections.unmodifiableSet(comments);
        this.isRepost = isRepost;
        this.originalPosterUsername = originalPosterUsername;
        this.likedUsersUsernames = likedUsersUsernames;
    }

    public Post(int id, long postedTime, String title, String content, String photoFilename, String posterUsername, Set<Comment> comments, Set<String> likedUsersUsernames) {
        this(id, postedTime, title, content, photoFilename, posterUsername, comments, false, "", likedUsersUsernames);
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
        result.put("originalPosterUsername", originalPosterUsername);
        result.put("comments", new JSONArray(
                this.comments.stream()
                .map(Comment::toJSON)
                .collect(Collectors.toList())
        ));
        result.put("likedUsersUsernames", new JSONArray(new ArrayList<>(this.likedUsersUsernames)));

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return id == post.id && postedTime == post.postedTime && isRepost == post.isRepost && title.equals(post.title) && content.equals(post.content) && photoFilename.equals(post.photoFilename) && posterUsername.equals(post.posterUsername) && comments.equals(post.comments) && originalPosterUsername.equals(post.originalPosterUsername) && likedUsersUsernames.equals(post.likedUsersUsernames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, postedTime, title, content, photoFilename, posterUsername, comments, isRepost, originalPosterUsername, likedUsersUsernames);
    }
}
