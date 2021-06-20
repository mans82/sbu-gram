package com.mans.sbugram.models.requests;

import org.json.JSONObject;

import java.util.Objects;

public class UserTimelineRequest extends Request {

    public final String username;
    public final String password;
    public final long fromTime;
    public final int count;

    public UserTimelineRequest(String username, String password, long fromTime, int count) {
        this.username = username;
        this.password = password;
        this.fromTime = fromTime;
        this.count = count;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();

        data.put("username", username);
        data.put("password", password);
        data.put("fromtime", fromTime);
        data.put("count", count);

        result.put("request_type", this.getRequestType().name());
        result.put("data", data);

        return result;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.USER_TIMELINE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserTimelineRequest request = (UserTimelineRequest) o;
        return fromTime == request.fromTime && count == request.count && username.equals(request.username) && password.equals(request.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, fromTime, count);
    }
}
