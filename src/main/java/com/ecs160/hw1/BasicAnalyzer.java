package com.ecs160.hw1;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BasicAnalyzer implements Analyzer {
    @Override
    public int getTopLevelPosts(List<Post> posts) {
        return posts.size(); // These are all the root posts
    }

    @Override
    public int getTotalPosts(List<Post> posts) {
        int total = posts.size();
        for (Post post : posts) {
            total += countReplies(post);
        }
        return total;
    }

    @Override
    public int getStandaloneTopLevelPosts(List<Post> posts) {
        return (int) posts.stream()
            .filter(post -> post.getReplies().isEmpty())
            .count();
    }

    @Override
    public int getTopLevelPostsWithReplies(List<Post> posts) {
        return (int) posts.stream()
            .filter(post -> !post.getReplies().isEmpty())
            .count();
    }

    private int countReplies(Post post) {
        return post.getReplies().size() + 
               post.getReplies().stream()
                   .mapToInt(this::countReplies)
                   .sum();
    }

    @Override
    public double getAverageReplies(List<Post> posts) {
        if (posts.isEmpty()) return 0;
        
        int totalReplies = posts.stream()
            .mapToInt(this::countReplies)
            .sum();
            
        return (double) totalReplies / posts.size();
    }

    @Override
    public Duration getAverageReplyInterval(List<Post> posts) {
        List<Duration> intervals = new ArrayList<>();
        
        for (Post post : posts) {
            collectIntervals(post, intervals);
        }
        
        if (intervals.isEmpty()) return Duration.ZERO;
        
        long totalSeconds = intervals.stream()
            .mapToLong(Duration::getSeconds)
            .sum();
            
        return Duration.ofSeconds(totalSeconds / intervals.size());
    }

    private void collectIntervals(Post post, List<Duration> intervals) {
        if (post.getReplies().isEmpty()) return;
        
        Instant parentTime = post.getCreatedAt();
        for (Post reply : post.getReplies()) {
            intervals.add(Duration.between(parentTime, reply.getCreatedAt()));
            collectIntervals(reply, intervals);
        }
    }
} 