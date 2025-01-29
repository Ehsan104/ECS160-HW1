package com.ecs160.hw1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BasicAnalyzerTest {
    private BasicAnalyzer analyzer;
    private List<Post> testPosts;

    @BeforeEach
    void setUp() {
        analyzer = new BasicAnalyzer();
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
        Post post = new Post("1", "Test post", Instant.now(), 0);
        testPosts.add(post);
        
        assertEquals(1, analyzer.getTotalPosts(testPosts));
        assertEquals(0.0, analyzer.getAverageReplies(testPosts));
        assertEquals(Duration.ZERO, analyzer.getAverageReplyInterval(testPosts));
    }

    @Test
    void testPostWithReplies() {
        Instant now = Instant.now();
        Post post = new Post("1", "Parent post", now, 2);
        Post reply1 = new Post("2", "Reply 1", now.plusSeconds(60), 0);
        Post reply2 = new Post("3", "Reply 2", now.plusSeconds(120), 0);
        
        post.addReply(reply1);
        post.addReply(reply2);
        testPosts.add(post);
        
        assertEquals(3, analyzer.getTotalPosts(testPosts));
        assertEquals(2.0, analyzer.getAverageReplies(testPosts));
        assertEquals(Duration.ofSeconds(90), analyzer.getAverageReplyInterval(testPosts));
    }

    @Test
    void testNestedReplies() {
        Instant now = Instant.now();
        Post post = new Post("1", "Parent post", now, 1);
        Post reply1 = new Post("2", "Reply 1", now.plusSeconds(60), 1);
        Post nestedReply = new Post("3", "Nested reply", now.plusSeconds(120), 0);
        
        reply1.addReply(nestedReply);
        post.addReply(reply1);
        testPosts.add(post);
        
        assertEquals(3, analyzer.getTotalPosts(testPosts));
        assertEquals(2.0, analyzer.getAverageReplies(testPosts));
    }
}
