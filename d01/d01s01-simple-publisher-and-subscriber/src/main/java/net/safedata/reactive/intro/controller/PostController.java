package net.safedata.reactive.intro.controller;

import net.safedata.reactive.intro.dto.PostDTO;
import net.safedata.reactive.intro.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
