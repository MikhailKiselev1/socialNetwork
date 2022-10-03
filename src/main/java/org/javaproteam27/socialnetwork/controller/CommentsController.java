package org.javaproteam27.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.aop.InfoLogger;
import org.javaproteam27.socialnetwork.model.dto.request.CommentRq;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.CommentRs;
import org.javaproteam27.socialnetwork.service.CommentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/post/{id}/comments")
@InfoLogger
public class CommentsController {
    private final CommentService commentService;

    @PostMapping
    public ResponseRs<CommentRs> createComment(@PathVariable(value = "id") int postId,
                                                               @RequestBody CommentRq commentRq){

        return commentService.addComment(postId, commentRq.getCommentText(),
                commentRq.getParentId());
    }

    @GetMapping
    public ListResponseRs<CommentRs> getComments (@PathVariable(value = "id") int postId,
                                                                  @RequestParam (name = "offset", defaultValue = "0") int offset,
                                                                  @RequestParam (name = "perPage", defaultValue = "20") int itemPerPage) {

        return commentService.getCommentsByPostIdInResponse(postId, offset, itemPerPage);
    }

    @DeleteMapping("/{comment_id}")
    public ResponseRs<CommentRs> deleteComment(@PathVariable(value = "id") int postId,
                                               @PathVariable(value = "comment_id") int commentId){

        return commentService.deleteComment(postId, commentId);
    }

    @PutMapping("/{comment_id}")
    public ResponseRs<CommentRs> editComment(@PathVariable(value = "id") int postId,
                                             @PathVariable(value = "comment_id") int commentId,
                                             @RequestBody CommentRq commentRq){
        return commentService.editComment(postId, commentId, commentRq.getCommentText(),commentRq.getParentId());
    }
}
