package net.safedata.reactive.intro.service;

import net.safedata.reactive.intro.dto.PostDTO;
import net.safedata.reactive.intro.subscriber.PostSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PostSubscribingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostSubscribingService.class);

    final WebClient webClient = WebClient.create("http://localhost:8080");

    @Async
    @Scheduled(fixedRate = 5000)
    public void initialize() {
        webClient.get()
                 .uri("/post")
                 .accept(MediaType.APPLICATION_JSON_UTF8)
                 .retrieve()
                 .bodyToFlux(PostDTO.class)
                 .subscribe(new PostSubscriber("subscriber", 2));
        System.out.println("-------------------------------------------------------------------------------------------");

        LOGGER.trace("Successfully subscribed");
    }
}
