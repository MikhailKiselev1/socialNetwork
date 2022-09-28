package org.javaproteam27.socialnetwork.controller;

import com.dropbox.core.DbxException;
import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.aop.InfoLogger;
import org.javaproteam27.socialnetwork.model.dto.request.PostRq;
import org.javaproteam27.socialnetwork.model.dto.request.UserRq;
import org.javaproteam27.socialnetwork.model.dto.response.*;
import org.javaproteam27.socialnetwork.service.LoginService;
import org.javaproteam27.socialnetwork.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.javaproteam27.socialnetwork.service.PersonService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@InfoLogger
public class UserController {

    private final PersonService personService;
    private final LoginService loginService;
    private final PostService postService;

    @GetMapping("/search")
    public ResponseEntity<ListResponseRs<PersonRs>> searchPeople(
            @RequestParam(value = "first_name", required = false) String firstName,
            @RequestParam(value = "last_name", required = false) String lastName,
            @RequestParam(value = "age_from", required = false) Integer ageFrom,
            @RequestParam(value = "age_to", required = false) Integer ageTo,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "perPage", required = false, defaultValue = "20") int itemPerPage) {

        return ResponseEntity.ok(personService.findPerson(firstName, lastName, ageFrom, ageTo, city, country,
                offset, itemPerPage));
    }

    @GetMapping("me")
    public ResponseRs<PersonRs> profileResponse(@RequestHeader("Authorization") String token) throws IOException, DbxException {
        return loginService.profileResponse(token);
    }

    @PutMapping("/me")
    public ResponseEntity<UserRs> editUser(@RequestBody UserRq request, @RequestHeader("Authorization") String token) {
        return personService.editUser(request, token);
    }

    @PostMapping("/{id}/wall")
    public ResponseRs<PostRs> publishPost(@RequestParam(required = false) Long publish_date,
                                          @RequestBody PostRq postRq, @PathVariable(value = "id") int authorId) {

        return postService.publishPost(publish_date, postRq, authorId);
    }

    @GetMapping("/{id}/wall")
    public ListResponseRs<PostRs> getUserPosts(@PathVariable(value = "id") int authorId,
                                               @RequestParam (defaultValue = "0") int offset,
                                               @RequestParam (defaultValue = "20") int itemPerPage) {

        return postService.findAllUserPosts(authorId, offset, itemPerPage);
    }

    @GetMapping("/{id}")
    public ResponseRs<PersonRs> getUserInfo(@PathVariable(value = "id") int userId) {
        return personService.getUserInfo(userId);
    }
}
