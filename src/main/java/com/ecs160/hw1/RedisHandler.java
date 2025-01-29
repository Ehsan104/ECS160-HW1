package com.ecs160.hw1;

import redis.clients.jedis.Jedis;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisHandler implements AutoCloseable {
    private final Jedis jedis;
    private int currentId = 0;

    public RedisHandler() {
        this.jedis = new Jedis("localhost", 6379);
    }

    public void storePost(Post post) {
        String postId = String.valueOf(++currentId);
        
        // Store post details
        Map<String, String> postData = new HashMap<>();
        postData.put("id", post.getId());
        postData.put("text", post.getText());
        postData.put("createdAt", post.getCreatedAt().toString());
        postData.put("replyCount", String.valueOf(post.getReplyCount()));
        
        jedis.hmset("post:" + postId, postData);
        
        // Store reply relationships
        if (!post.getReplies().isEmpty()) {
            for (Post reply : post.getReplies()) {
                String replyId = String.valueOf(++currentId);
                jedis.sadd("post:" + postId + ":replies", replyId);
                storePost(reply);
            }
        }
    }

    public void storePosts(List<Post> posts) {
        for (Post post : posts) {
            storePost(post);
        }
    }

    @Override
    public void close() {
        if (jedis != null) {
            jedis.close();
        }
    }
} 