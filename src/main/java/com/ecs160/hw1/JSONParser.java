package com.ecs160.hw1;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class JSONParser {
    public List<Post> parseJsonFromFile(String filePath) throws FileNotFoundException {
        JsonElement element = JsonParser.parseReader(new java.io.FileReader(filePath));
        return parseJsonElement(element);
    }

    public List<Post> parseJson() {
        JsonElement element = JsonParser.parseReader(new InputStreamReader(
                JSONParser.class.getClassLoader().getResourceAsStream("input.json")));
        return parseJsonElement(element);
    }

    private List<Post> parseJsonElement(JsonElement element) {
        List<Post> posts = new ArrayList<>();
        
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            JsonArray feedArray = jsonObject.get("feed").getAsJsonArray();
            
            for (JsonElement feedElement : feedArray) {
                if (feedElement.getAsJsonObject().has("thread")) {
                    JsonObject thread = feedElement.getAsJsonObject().get("thread").getAsJsonObject();
                    Post post = parseThread(thread);
                    if (post != null) {
                        posts.add(post);
                    }
                }
            }
        }
        
        return posts;
    }

    private Post parseThread(JsonObject thread) {
        if (!thread.has("post")) return null;
        
        JsonObject postObj = thread.get("post").getAsJsonObject();
        Post post = parsePost(postObj);
        
        if (thread.has("replies")) {
            JsonArray replies = thread.get("replies").getAsJsonArray();
            for (JsonElement replyElement : replies) {
                JsonObject replyThread = replyElement.getAsJsonObject();
                if (replyThread.has("post")) {
                    JsonObject replyObj = replyThread.getAsJsonObject().get("post").getAsJsonObject();
                    Post reply = parsePost(replyObj);
                    if (reply != null) {
                        post.addReply(reply);
                        
                        if (replyThread.has("replies")) {
                            parseReplies(replyThread.get("replies").getAsJsonArray(), reply);
                        }
                    }
                }
            }
        }
        
        return post;
    }

    private void parseReplies(JsonArray replies, Post parentPost) {
        for (JsonElement replyElement : replies) {
            JsonObject replyThread = replyElement.getAsJsonObject();
            if (replyThread.has("post")) {
                JsonObject replyObj = replyThread.get("post").getAsJsonObject();
                Post reply = parsePost(replyObj);
                if (reply != null) {
                    parentPost.addReply(reply);
                    
                    if (replyThread.has("replies")) {
                        parseReplies(replyThread.get("replies").getAsJsonArray(), reply);
                    }
                }
            }
        }
    }

    private Post parsePost(JsonObject postObj) {
        String id = postObj.get("uri").getAsString();
        String text = postObj.get("record").getAsJsonObject().get("text").getAsString();
        Instant createdAt = Instant.parse(postObj.get("indexedAt").getAsString());
        int replyCount = postObj.has("replyCount") ? postObj.get("replyCount").getAsInt() : 0;
        
        return new Post(id, text, createdAt, replyCount);
    }
} 