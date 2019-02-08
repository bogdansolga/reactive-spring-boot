package net.safedata.reactive.spring.service;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.AsPublisher;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Service
public class TweetService {
    private final ActorMaterializer actorMaterializer;
    private final TweetRepository tweetRepository;

    @Autowired
    public TweetService(final ActorMaterializer actorMaterializer, final TweetRepository tweetRepository) {
        this.actorMaterializer = actorMaterializer; this.tweetRepository = tweetRepository;
    }

    Publisher<Tweet> getAllTweets() {
        return tweetRepository.findAll();
    }

    Publisher<HashTag> getAllHashTags() {
        return Source.fromPublisher(getAllTweets())
                     .map(Tweet::getHashTags)
                     .reduce(this::joinSets)
                     .mapConcat(it -> it)
                     .runWith(Sink.asPublisher(AsPublisher.WITH_FANOUT), actorMaterializer);
    }

    private Set<HashTag> joinSets(Set<HashTag> first, Set<HashTag> second) {
        final Set<HashTag> hashTags = new HashSet<>();
        hashTags.addAll(first);
        hashTags.addAll(second);
        return hashTags;
    }
}

@Configuration
class TweetServiceConfiguration {
    @Bean
    ActorSystem actorSystem() {
        return ActorSystem.create("akka-streaming-example");
    }

    @Bean
    ActorMaterializer actorMaterializer() {
        return ActorMaterializer.create(actorSystem());
    }

    @Bean
    RouterFunction<ServerResponse> routes(final TweetService tweetService) {
        return route(GET("/tweets"), request -> ok().body(tweetService.getAllTweets(), Tweet.class))
                .andRoute(GET("/hashTags"), request -> ok().body(tweetService.getAllHashTags(), HashTag.class));
    }

    @Bean
    @ConditionalOnProperty("testTweets")
    ApplicationRunner runner(final TweetRepository tweetRepository) {
        final Author bogdan = new Author("bogdan");
        final Author vio = new Author("vio");

        final Flux<Tweet> tweets = Flux.just(
                new Tweet("#2x2 was the best #marathon for #2018", bogdan),
                new Tweet("#Scenic will be the greatest #marathon of #2019", bogdan),
                new Tweet("In #2019 I will run my first #100k", bogdan),
                new Tweet("I will also run #Scenic and I will improve my #MPC time in #2019", vio)
        );

        return args ->
            tweetRepository.deleteAll()
                           .thenMany(tweetRepository.saveAll(tweets))
                           .thenMany(tweetRepository.findAll())
                           .subscribe(System.out::println);
    }
}

interface TweetRepository extends ReactiveMongoRepository<Tweet, String> {
}

@Document
class Author {
    private final String name;

    Author(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return Objects.equals(name, author.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

@Document
class Tweet {

    @Id
    private String id;
    private String text;
    private Author author;

    public Tweet() {
    }

    Tweet(String text, Author author) {
        this.text = text;
        this.author = author;
    }

    public Tweet(String id, String text, Author author) {
        this.id = id;
        this.text = text;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    Set<HashTag> getHashTags() {
        return Arrays.stream(text.split("\\s"))
                     .filter(text -> text.startsWith("#"))
                     .map(word -> new HashTag(word.replaceAll("[^#\\w+]", "").toLowerCase()))
                     .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tweet tweet = (Tweet) o;
        return Objects.equals(id, tweet.id) &&
                Objects.equals(text, tweet.text) &&
                Objects.equals(author, tweet.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, author);
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", author=" + author.getName() +
                '}';
    }
}

@Document
class HashTag {

    @Id
    private final String id;

    HashTag(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashTag hashTag = (HashTag) o;
        return Objects.equals(id, hashTag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}