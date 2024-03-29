package com.redhat.developer.raffle;

import twitter4j.Query;

import java.util.*;
import java.util.stream.Collectors;

public class TwitterQueryBuilder {

    private final Set<String> usernames = new HashSet<>();

    private final Set<String> hashtags = new HashSet<>();

    private boolean onlyTweetsWithMedia = true;

    private TwitterQueryBuilder(Collection<String> usernames) {
        this.usernames.addAll(usernames);
    }

    public static TwitterQueryBuilder of(String... usernames) {
        List<String> filteredUsernames = Arrays.stream(usernames).map(s -> s.replaceAll("@", ""))
                .filter(s -> !s.isEmpty()).collect(Collectors.toList());
        return new TwitterQueryBuilder(filteredUsernames);
    }

    public TwitterQueryBuilder addUsername(String username) {
        Objects.requireNonNull(username);
        String filtered = username.replaceAll("@", "");
        if (!filtered.isEmpty()) {
            usernames.add(filtered);
        }
        return this;
    }

    public TwitterQueryBuilder addHashtag(String hashtag) {
        Objects.requireNonNull(hashtag);
        hashtags.add(hashtag);
        return this;
    }

    public TwitterQueryBuilder addHashtags(String... hashtags) {
        List<String> filteredHashtags = Arrays.stream(hashtags).map(s -> s.replaceAll("#", ""))
                .filter(s -> !s.isEmpty()).collect(Collectors.toList());
        this.hashtags.addAll(filteredHashtags);
        return this;
    }

    public TwitterQueryBuilder onlyTweetsWithMedia() {
        this.onlyTweetsWithMedia = true;
        return this;
    }

    public TwitterQueryBuilder anyTweet() {
        this.onlyTweetsWithMedia = false;
        return this;
    }

    public Query build() {
        StringBuilder sb = new StringBuilder();
        if (!usernames.isEmpty()) {
            sb.append(String.join(" ", usernames.stream()
                    .map(s -> String.format("@%s", s))
                    .collect(Collectors.toList())));
        }
        if (!hashtags.isEmpty()) {
            sb.append(" ");
            sb.append(hashtags.stream().
                    distinct()
                    .map(s -> String.format("#%s", s))
                    .collect(Collectors.joining(",")));
        }
        if (onlyTweetsWithMedia) {
            sb.append(" filter:media");
        }
        sb.append(" -filter:retweets");
        return new Query(sb.toString());
    }

}