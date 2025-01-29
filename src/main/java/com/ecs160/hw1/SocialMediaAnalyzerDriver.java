package com.ecs160.hw1;

import org.apache.commons.cli.*;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.List;

public class SocialMediaAnalyzerDriver {
    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("w", "weighted", true, "Use weighted average calculation");
        options.addOption("f", "file", true, "Input JSON file path");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            
            boolean weighted = Boolean.parseBoolean(cmd.getOptionValue("weighted", "false"));
            String filePath = cmd.getOptionValue("file");
            
            parse_json jsonParser = new parse_json();
            List<Post> posts;
            
            if (filePath != null) {
                try {
                    posts = jsonParser.parseJsonFromFile(filePath);
                } catch (FileNotFoundException e) {
                    System.err.println("Error: Input file not found: " + filePath);
                    System.exit(1);
                    return;
                }
            } else {
                posts = jsonParser.parseJson(); // Use default file
            }
            
            // Store posts in Redis
            try (RedisHandler redisHandler = new RedisHandler()) {
                redisHandler.storePosts(posts);
                
                // Choose analyzer based on weighted flag
                Analyzer analyzer = weighted ? new WeightedAnalyzer() : new BasicAnalyzer();
                
                int topLevelPosts = analyzer.getTopLevelPosts(posts);
                int totalPosts = analyzer.getTotalPosts(posts);
                double avgReplies = analyzer.getAverageReplies(posts);
                Duration avgInterval = analyzer.getAverageReplyInterval(posts);
                
                System.out.println("Top-level posts: " + topLevelPosts);
                System.out.println("Total posts (including all replies): " + totalPosts);
                System.out.println("Average number of replies per top-level post: " + String.format("%.2f", avgReplies));
                System.out.println("Average duration between replies: " + 
                    String.format("%02d:%02d:%02d", 
                        avgInterval.toHours(),
                        avgInterval.toMinutesPart(),
                        avgInterval.toSecondsPart()));
            } catch (Exception e) {
                System.err.println("Error: Failed to connect to Redis server: " + e.getMessage());
                System.exit(1);
            }
            
        } catch (ParseException e) {
            System.err.println("Error parsing command line arguments: " + e.getMessage());
            System.exit(1);
        }
    }
}
