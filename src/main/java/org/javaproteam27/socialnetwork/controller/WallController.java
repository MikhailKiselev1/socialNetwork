package org.javaproteam27.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.PostRq;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.PostRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.service.PostService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/{id}/wall")
public class WallController {
    private final PostService postService;
    @PostMapping
    public ResponseRs<PostRs> publishPost(
            @RequestParam(required = false) Long publish_date,
            @RequestBody PostRq postRq,
            @PathVariable(value = "id") int authorId){

        return postService.publishPost(publish_date, postRq, authorId);
    }

    @GetMapping
    public ListResponseRs<PostRs> getUserPosts(
            @PathVariable(value = "id") int authorId,
            @RequestParam (defaultValue = "0") int offset,
            @RequestParam (defaultValue = "20") int itemPerPage) {

        return postService.findAllUserPosts(authorId, offset, itemPerPage);
    }
}