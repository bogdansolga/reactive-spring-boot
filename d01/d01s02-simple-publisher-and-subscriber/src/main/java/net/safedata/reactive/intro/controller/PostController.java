package net.safedata.reactive.intro.controller;

import net.safedata.reactive.intro.dto.PostDTO;
import net.safedata.reactive.intro.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {
    
    private final PostService postService;

    @Autowired
    public PostController(final PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<PostDTO> posts() {
        return postService.getRandomPosts();
    }

    @GetMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public Flux<PostDTO> infiniteStreamOfPosts() {
        return Flux.<PostDTO>generate(sink -> sink.next(postService.getRandomPost()))
                   .delayElements(Duration.ofMillis(100));
    }
}
