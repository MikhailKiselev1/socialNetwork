package org.javaproteam27.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.PostRs;
import org.javaproteam27.socialnetwork.service.PostService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/feeds")
@RequiredArgsConstructor
public class FeedsController {
    private final PostService postService;

    @GetMapping
    public ListResponseRs<PostRs> getAllPost(
            @RequestParam (defaultValue = "0") Integer offset,
            @RequestParam (defaultValue = "20") Integer perPage) { //perPage

        return postService.findAllPosts(offset, perPage);
    }
}
