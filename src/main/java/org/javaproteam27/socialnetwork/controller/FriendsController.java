package org.javaproteam27.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.response.FriendshipRs;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.PersonRs;
import org.javaproteam27.socialnetwork.model.entity.Friendship;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.model.enums.FriendshipStatusCode;
import org.javaproteam27.socialnetwork.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendsController {

    private final FriendsService friendsService;
    private final FriendshipStatusService friendshipStatusService;
    private final FriendshipService friendshipService;
    private final PersonService personService;

    @GetMapping("/recommendations")
    private ResponseEntity<ListResponseRs<PersonRs>> getRecommendations(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "perPage", defaultValue = "10") int itemPerPage) {

        ListResponseRs<PersonRs> recommendations = friendsService.getRecommendations(token, offset, itemPerPage);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping
    private ResponseEntity<ListResponseRs<PersonRs>> getListFriends(
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "perPage", required = false, defaultValue = "20") int itemPerPage){

        ListResponseRs<PersonRs> listFriends = friendsService.getListFriends(name,offset,itemPerPage);

        return  ResponseEntity.ok(listFriends);
    }

    @PostMapping("/{id}")
    private ResponseEntity<FriendshipRs> addFriends(@PathVariable int id){
        Person person = personService.getAuthorizedPerson();
        int friendshipStatusId = 0;
        if (friendshipService.requestVerification(id, person.getId()).size() == 0){
            friendshipStatusId = friendshipStatusService.addStatus();
            return ResponseEntity.ok(friendshipService.addFriendShip(id, friendshipStatusId,person.getId()));
        }else {
            friendshipStatusId = -1;
        }

        return ResponseEntity.ok(friendshipService.addFriendShip(id, friendshipStatusId,person.getId()));
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<FriendshipRs> deleteFriends(@PathVariable int id){

        Person person = personService.getAuthorizedPerson();
        int srcPersonId = person.getId();

        List<Friendship> friendshipList =friendshipService.findByFriendShip(srcPersonId, id);
        friendshipService.deleteFriendShip(srcPersonId,id);

        return ResponseEntity.ok(friendshipStatusService.deleteStatus(friendshipList));
    }

    @GetMapping("/request")
    private ResponseEntity<ListResponseRs<PersonRs>> getListApplicationsFriends(
            @RequestParam(value = "name",required = false) String name,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "perPage", required = false, defaultValue = "20") int itemPerPage){

        ListResponseRs<PersonRs> listFriends = friendsService.getListApplicationsFriends(name,offset,itemPerPage);

        return  ResponseEntity.ok(listFriends);
    }

    @PostMapping("/request/{id}")
    private ResponseEntity<FriendshipRs> addApplicationsFriends(@PathVariable int id){

        Person person = personService.getAuthorizedPerson();
        return ResponseEntity.ok(friendshipStatusService.updateStatus(person.getId(), id, FriendshipStatusCode.FRIEND));
    }

    @DeleteMapping("/request/{id}")
    private ResponseEntity<FriendshipRs> deleteApplicationsFriends(@PathVariable int id){

        Person person = personService.getAuthorizedPerson();
        return ResponseEntity.ok(friendshipStatusService.updateStatus(person.getId(), id, FriendshipStatusCode.DECLINED));
    }
}
