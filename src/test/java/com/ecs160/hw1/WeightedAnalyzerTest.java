package com.ecs160.hw1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WeightedAnalyzerTest {
    private WeightedAnalyzer analyzer;
    private List<Post> testPosts;

    @BeforeEach
    void setUp() {
        analyzer = new WeightedAnalyzer();
        testPosts = new ArrayList<>();
    }

    @Test
    void testEmptyPosts() {
        assertEquals(0, analyzer.getTotalPosts(testPosts));
        assertEquals(0.0, analyzer.getAverageReplies(testPosts));
        assertEquals(Duration.ZERO, analyzer.getAverageReplyInterval(testPosts));
    }

    @Test
    void testSinglePostNoReplies() {
        Post post = new Post("1", "Test post with five words", Instant.now(), 0);
        testPosts.add(post);
        
        assertEquals(2, analyzer.getTotalPosts(testPosts)); // 1 + (5/5) = 2
        assertEquals(0.0, analyzer.getAverageReplies(testPosts));
    }

    @Test
    void testPostsWithDifferentLengths() {
        Post post1 = new Post("1", "Short post", Instant.now(), 0);
        Post post2 = new Post("2", "This is a much longer post with more words", Instant.now(), 0);
        
        testPosts.add(post1);
        testPosts.add(post2);
        
        // post1 weight = 1 + (2/9) ≈ 1.22
        // post2 weight = 1 + (9/9) = 2
        // Total ≈ 3.22
        assertEquals(3, analyzer.getTotalPosts(testPosts));
    }

    @Test
    void testWeightedReplies() {
        Instant now = Instant.now();
        Post post = new Post("1", "Main post", now, 2);
        Post reply1 = new Post("2", "Short reply", now.plusSeconds(60), 0);
        Post reply2 = new Post("3", "This is a much longer reply with many more words", now.plusSeconds(120), 0);
        
        post.addReply(reply1);
        post.addReply(reply2);
        testPosts.add(post);
        
        // Main post: 2 words, weight = 1 + (2/10) = 1.2
        // Reply1: 2 words, weight = 1 + (2/10) = 1.2
        // Reply2: 10 words, weight = 1 + (10/10) = 2
        // Total posts = 4.4
        // Average replies = (1.2 + 2)/1 = 3.2
        assertEquals(4, analyzer.getTotalPosts(testPosts));
        assertTrue(analyzer.getAverageReplies(testPosts) > 3.0);
    }
} 