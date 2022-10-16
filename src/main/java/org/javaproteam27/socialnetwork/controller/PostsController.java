package org.javaproteam27.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.PostRq;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.PostRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/post")
public class PostsController {
    private final PostService postService;

    @DeleteMapping("/{id}")
    public ResponseRs<PostRs> deletePost(@PathVariable(value = "id") int postId){

        return postService.softDeletePost(postId);
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
    public ListResponseRs<PostRs> findPost(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "date_from", required = false) String dateFrom,
            @RequestParam(value = "date_to", required = false) String dateTo,
            @RequestParam(value = "author", required = false) String authorName,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "perPage", required = false, defaultValue = "20") int itemPerPage) {

        return postService.findPost(text, dateFrom, dateTo, authorName, tags, offset, itemPerPage);
    }

    @PutMapping("/{id}/recover")
    public ResponseRs<PostRs> recoverPost(@PathVariable(value = "id") int postId) {

        return postService.recoverPost(postId);
    }
}
