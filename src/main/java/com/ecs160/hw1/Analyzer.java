package com.ecs160.hw1;

import java.time.Duration;
import java.util.List;

public interface Analyzer {
    int getTopLevelPosts(List<Post> posts);  // Only root/parent posts
    int getTotalPosts(List<Post> posts);     // All posts including replies
    int getStandaloneTopLevelPosts(List<Post> posts);  // Top-level posts without replies
    int getTopLevelPostsWithReplies(List<Post> posts); // Top-level posts with replies
    double getAverageReplies(List<Post> posts);
    Duration getAverageReplyInterval(List<Post> posts);
} 