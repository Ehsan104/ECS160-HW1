package com.ecs160.hw1;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class WeightedAnalyzer implements Analyzer {
    @Override
    public int getTopLevelPosts(List<Post> posts) {
        return posts.size();
    }

    @Override
    public int getTotalPosts(List<Post> posts) {
        if (posts.isEmpty()) return 0;
        
        // Find the post with maximum word count
        int maxWordCount = findMaxWordCount(posts);
        
        // Calculate weighted sum of all posts
        double weightedSum = calculateWeightedSum(posts, maxWordCount);
        
        return (int) Math.round(weightedSum);
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

    @Override
    public double getAverageReplies(List<Post> posts) {
        if (posts.isEmpty()) return 0;
        
        int maxWordCount = findMaxWordCount(posts);
        double totalWeightedReplies = 0;
        
        for (Post post : posts) {
            totalWeightedReplies += calculateWeightedRepliesForPost(post, maxWordCount);
        }
        
        return totalWeightedReplies / posts.size();
    }

    @Override
    public Duration getAverageReplyInterval(List<Post> posts) {
        // Interval calculation remains the same as BasicAnalyzer
        BasicAnalyzer basicAnalyzer = new BasicAnalyzer();
        return basicAnalyzer.getAverageReplyInterval(posts);
    }

    private int findMaxWordCount(List<Post> posts) {
        int maxWordCount = posts.stream()
            .mapToInt(Post::getWordCount)
            .max()
            .orElse(1);
            
        // Also check replies for max word count
        for (Post post : posts) {
            maxWordCount = Math.max(maxWordCount, findMaxWordCountInReplies(post));
        }
        
        return maxWordCount;
    }

    private int findMaxWordCountInReplies(Post post) {
        int maxInReplies = post.getReplies().stream()
            .mapToInt(Post::getWordCount)
            .max()
            .orElse(0);
            
        // Recursively check nested replies
        for (Post reply : post.getReplies()) {
            maxInReplies = Math.max(maxInReplies, findMaxWordCountInReplies(reply));
        }
        
        return maxInReplies;
    }

    private double calculateWeight(Post post, int maxWordCount) {
        return 1.0 + ((double) post.getWordCount() / maxWordCount);
    }

    private double calculateWeightedSum(List<Post> posts, int maxWordCount) {
        double sum = posts.stream()
            .mapToDouble(post -> calculateWeight(post, maxWordCount))
            .sum();
            
        // Add weights of all replies
        for (Post post : posts) {
            sum += calculateWeightedSumOfReplies(post, maxWordCount);
        }
        
        return sum;
    }

    private double calculateWeightedSumOfReplies(Post post, int maxWordCount) {
        double sum = post.getReplies().stream()
            .mapToDouble(reply -> calculateWeight(reply, maxWordCount))
            .sum();
            
        // Recursively add weights of nested replies
        for (Post reply : post.getReplies()) {
            sum += calculateWeightedSumOfReplies(reply, maxWordCount);
        }
        
        return sum;
    }

    private double calculateWeightedRepliesForPost(Post post, int maxWordCount) {
        return post.getReplies().stream()
            .mapToDouble(reply -> calculateWeight(reply, maxWordCount))
            .sum();
    }
} 