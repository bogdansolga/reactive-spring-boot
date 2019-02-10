package net.safedata.reactive.intro.service;

import net.safedata.reactive.intro.dto.PostDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class PostService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostService.class);

    private final ApplicationEventPublisher applicationEventPublisher;

    private List<String> posts;

    private List<String> authors = Arrays.asList("john", "jane", "alex", "maria", "michelle");

    private Random random;

    @Autowired
    public PostService(final ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Async
    @EventListener(ApplicationReadyEvent.class)
    public void loadPostsKeyWords() {
        posts = getPostsFromFile(new ClassPathResource("posts.txt"));
        LOGGER.trace("Successfully loaded {} posts from the posts file", posts.size());

        random = new Random(posts.size());

        applicationEventPublisher.publishEvent("Initialized");
    }

    private List<String> getPostsFromFile(final ClassPathResource classPathResource) {
        try (InputStream inputStream = classPathResource.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            return getLines(bufferedReader.lines());
        } catch (final IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IllegalArgumentException("Cannot read the file '" + classPathResource.getFilename() + "'");
        }
    }

    private List<String> getLines(final Stream<String> lines) {
        return lines.map(line -> new StringTokenizer(line, "\n"))
                    .flatMap(tokenizer -> getLines(tokenizer).stream())
                    .collect(Collectors.toList());
    }

    private Set<String> getLines(final StringTokenizer tokenizer) {
        final Set<String> lines = new HashSet<>();

        while (tokenizer.hasMoreTokens()) {
            lines.add(tokenizer.nextToken().trim());
        }

        return lines;
    }

    public PostDTO getRandomPost() {
        final String post = posts.get(random.nextInt(posts.size()));
        return new PostDTO(authors.get(random.nextInt(5)), post);
    }

    public List<PostDTO> getRandomPosts() {
        final int postsNumber = posts.size();
        final int start = random.nextInt(postsNumber);
        final int end = random.nextInt(postsNumber);

        final int startFrom = end >= start ? start : end;
        final int endAt = end > start ? end : start;

        final List<PostDTO> listOfPosts = IntStream.range(startFrom, endAt)
                                                   .mapToObj(index -> posts.get(index))
                                                   .map(post -> new PostDTO(authors.get(random.nextInt(5)), post))
                                                   .collect(Collectors.toList());
        LOGGER.debug("Returning {} posts [from {} to {}]", listOfPosts.size(), startFrom, endAt);
        return listOfPosts;
    }
}
