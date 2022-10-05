package org.javaproteam27.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.LikeRq;
import org.javaproteam27.socialnetwork.model.dto.response.LikeRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.service.LikeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikesController {
    private final LikeService likeService;

    @PutMapping
    public ResponseRs<LikeRs> putLike(@RequestBody LikeRq likeRq) {

        return likeService.addLike(likeRq.getType(), likeRq.getItemId());
    }

    @DeleteMapping
    public ResponseRs<LikeRs> deleteLike(@RequestParam("item_id") Integer itemId,
                                                         @RequestParam String type) {

        return likeService.deleteLike(type, itemId);
    }

    @GetMapping
    public ResponseRs<LikeRs> getLikeList(@RequestParam("item_id") Integer itemId,
                                                          @RequestParam String type) {

        return likeService.getLikeList(type, itemId);
    }
}
