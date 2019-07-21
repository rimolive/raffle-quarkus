package com.redhat.developer.raffle;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import twitter4j.QueryResult;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

@Path("/raffle")
public class RaffleResource {

    private static final int MAX_SIZE = 150;

    private final Twitter twitter = new TwitterFactory().getInstance();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    @GET
    @Path("/{csvHashtags}/{csvUsernames}")
    public List<RaffleResponse> hashtagsAndUsernames(@PathParam("csvUsernames") String[] csvUsernames,
                                                     @PathParam("csvHashtags") String[] csvHashtags) throws Exception {
        QueryResult queryResult = twitter.search(TwitterQueryBuilder.of(csvUsernames).addHashtags(csvHashtags).build());
        return queryResult.getTweets().stream()
                .filter(s -> s.getCreatedAt().toInstant().isAfter(Instant.now().minus(180, ChronoUnit.MINUTES)))
                .map(s -> RaffleResponse.of(s.getUser().getScreenName(), s.getId()))
                .distinct()
                .limit(MAX_SIZE)
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(ArrayList::new),
                        list -> {
                            Collections.shuffle(list);
                            return list.stream();
                        }
                ))
                .limit(25)
                .collect(Collectors.toList());
    }

}