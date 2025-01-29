package com.ecs160.hw1;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private String id;
    private String text;
    private Instant createdAt;
    private List<Post> replies;
    private int replyCount;

    public Post(String id, String text, Instant createdAt, int replyCount) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.replyCount = replyCount;
        this.replies = new ArrayList<>();
    }

    public void addReply(Post reply) {
        replies.add(reply);
    }

    public String getId() { return id; }
    public String getText() { return text; }
    public Instant getCreatedAt() { return createdAt; }
    public List<Post> getReplies() { return replies; }
    public int getReplyCount() { return replyCount; }
    
    public int getWordCount() {
        return text.split("\\s+").length;
    }
} 