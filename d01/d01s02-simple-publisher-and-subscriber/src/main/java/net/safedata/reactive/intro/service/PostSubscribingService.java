package net.safedata.reactive.intro.service;

import net.safedata.reactive.intro.dto.PostDTO;
import net.safedata.reactive.intro.subscriber.PostSubscriber;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PostSubscribingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostSubscribingService.class);

    private PostSubscriber postSubscriber;
    private WebClient webClient;

    @Async
    @EventListener(String.class)
    public void initialize() {
        postSubscriber = new PostSubscriber("subscriber", 3);
        webClient = WebClient.create("http://localhost:8080");
    }

    @Async
    @Scheduled(fixedRate = 5000)
    public void getPosts() {
        if (webClient == null) return; // avoid a NPE on init

        final Publisher<PostDTO> postPublisher = getPostPublisher();
        postPublisher.subscribe(postSubscriber);
        System.out.println("-------------------------------------------------------------------------------------------");

        LOGGER.trace("Successfully subscribed");
    }

    private Publisher<PostDTO> getPostPublisher() {
        return webClient.get()
                        .uri("/post")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .retrieve()
                        .bodyToFlux(PostDTO.class);
    }
}
