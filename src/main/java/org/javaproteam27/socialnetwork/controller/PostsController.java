package org.javaproteam27.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.PostRq;
import org.javaproteam27.socialnetwork.model.dto.response.PostRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/post")
public class PostsController {
    private final PostService postService;

    @DeleteMapping("/{id}")
    public ResponseRs<PostRs> deletePost(@PathVariable(value = "id") int postId){

        return postService.deletePost(postId);
    }

    @PutMapping("/{id}")
    public ResponseRs<PostRs> updatePost(@PathVariable(value = "id") int postId,
                                                        @RequestBody PostRq postRq){

        return postService.updatePost(postId, postRq.getTitle(),
                postRq.getPostText(), postRq.getTags());
    }

    @GetMapping("/{id}")
    public ResponseRs<PostRs> getPost(@PathVariable(value = "id") int postId){

        return postService.getPost(postId);
    }

    @GetMapping
    public ResponseEntity<?> findPost(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "date_from", required = false) Long dateFrom,
            @RequestParam(value = "date_to", required = false) Long dateTo,
            @RequestParam(value = "author", required = false) String authorName,
            @RequestParam(value = "tag", required = false) List<String> tags,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "perPage", required = false, defaultValue = "20") int itemPerPage) {

        return postService.findPost(text, dateFrom, dateTo, authorName, tags, offset, itemPerPage);
    }
}
