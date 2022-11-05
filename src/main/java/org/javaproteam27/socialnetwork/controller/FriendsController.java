package org.javaproteam27.socialnetwork.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.aop.InfoLogger;
import org.javaproteam27.socialnetwork.model.dto.response.FriendshipRs;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.PersonRs;
import org.javaproteam27.socialnetwork.service.FriendsService;
import org.javaproteam27.socialnetwork.service.FriendshipService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
@InfoLogger
@Tag(name = "friends", description = "Взаимодействие с друзьями")
public class FriendsController {

    private final FriendsService friendsService;
    private final FriendshipService friendshipService;

    @GetMapping("/recommendations")
    public ListResponseRs<PersonRs> getRecommendations(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "perPage", defaultValue = "10") int itemPerPage) {

        return friendsService.getRecommendations(token, offset, itemPerPage);
    }

    @GetMapping
    public ListResponseRs<PersonRs> getListFriends(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "perPage", required = false, defaultValue = "20") int itemPerPage) {

        return friendsService.getListFriends(offset, itemPerPage);
    }

    @PostMapping("/{id}")
    public FriendshipRs addFriends(@PathVariable int id) {

        return friendshipService.addFriendShip(id);
    }

    @DeleteMapping("/{id}")
    public FriendshipRs deleteFriends(@PathVariable int id) {

        return friendshipService.deleteFriend(id);
    }

    @GetMapping("/request")
    public ListResponseRs<PersonRs> getListApplicationsFriends(
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "perPage", required = false, defaultValue = "20") int itemPerPage) {

        return friendsService.getListApplicationsFriends(offset, itemPerPage);
    }

    @PostMapping("/request/{id}")
    public FriendshipRs addApplicationsFriends(@PathVariable int id) {

        return friendshipService.addApplicationsFriends(id);
    }

    @DeleteMapping("/request/{id}")
    public FriendshipRs deleteApplicationsFriends(@PathVariable int id) {

        return friendshipService.deleteApplicationsFriends(id);
    }
}
