package com.example.george.traffictracker.model;

/**
 * Created by george on 11/6/17.
 */

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {

    public String key;
    public String uid;
    public String title;
    public String category;
    public String body;
    public Long timestamp;
    public String distance;
    private String messageText;
    private String messageUser;
    private String messageUserId;
    private long messageTime;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String key, String uid, long messageTime, String messageText) {
        this.key = key;
        this.uid = uid;
        this.messageTime = messageTime;
        this.messageText = messageText;
    }

    public Post(String key, String uid, String messageUser, String messageText) {
        this.key = key;
        this.uid = uid;
        this.messageUser = messageUser;
        this.messageText = messageText;
    }

    public Post(String key, String uid, String title, String messageText, String messageUserId) {
        this.key = key;
        this.uid = uid;
        this.title = title;
        this.messageText = messageText;
        this.messageUserId = messageUserId;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("key", key);
        result.put("uid", messageUserId);
        result.put("timestamp", messageTime);
        result.put("user", messageUser);
        result.put("messageText", messageText);

        return result;
    }


}

