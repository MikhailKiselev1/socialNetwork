package org.javaproteam27.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.LikeRq;
import org.javaproteam27.socialnetwork.model.dto.response.LikeRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikesController {
    private final LikeService likeService;

    @PutMapping
    public ResponseEntity<ResponseRs<LikeRs>> putLike(@RequestBody LikeRq likeRq){
        ResponseRs<LikeRs> responseRs = likeService.addLike(likeRq.getType(), likeRq.getItemId());
        return ResponseEntity.ok(responseRs);
    }

    @DeleteMapping
    public ResponseEntity<ResponseRs<LikeRs>> deleteLike(@RequestParam("item_id") Integer itemId,
                                                         @RequestParam String type){
        ResponseRs<LikeRs> responseRs = likeService.deleteLike(type, itemId);
        return ResponseEntity.ok(responseRs);
    }

    @GetMapping
    public ResponseEntity<ResponseRs<LikeRs>> getLikeList(@RequestParam("item_id") Integer itemId,
                                                          @RequestParam String type){
        ResponseRs<LikeRs> responseRs = likeService.getLikeList(type, itemId);
        return ResponseEntity.ok(responseRs);
    }
}
