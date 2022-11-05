package org.javaproteam27.socialnetwork.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.aop.InfoLogger;
import org.javaproteam27.socialnetwork.model.dto.request.PostRq;
import org.javaproteam27.socialnetwork.model.dto.request.UserRq;
import org.javaproteam27.socialnetwork.model.dto.response.*;
import org.javaproteam27.socialnetwork.service.LoginService;
import org.javaproteam27.socialnetwork.service.PersonService;
import org.javaproteam27.socialnetwork.service.PostService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@InfoLogger
@Tag(name = "users", description = "Взаимодействие с пользователями")
public class UserController {

    private final PersonService personService;
    private final LoginService loginService;
    private final PostService postService;

    @GetMapping("/search")
    public ListResponseRs<PersonRs> searchPeople(
            @RequestParam(value = "first_name", required = false) String firstName,
            @RequestParam(value = "last_name", required = false) String lastName,
            @RequestParam(value = "age_from", required = false) Integer ageFrom,
            @RequestParam(value = "age_to", required = false) Integer ageTo,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "offset", required = false, defaultValue = "0") int offset,
            @RequestParam(value = "perPage", required = false, defaultValue = "20") int itemPerPage) {

        return personService.findPerson(firstName, lastName, ageFrom, ageTo, city, country,
                offset, itemPerPage);
    }

    @GetMapping("me")
    public ResponseRs<PersonRs> profileResponse(@RequestHeader("Authorization") String token) {
        return loginService.profileResponse(token);
    }

    @PutMapping("/me")
    public UserRs editUser(@RequestBody UserRq request, @RequestHeader("Authorization") String token) {
        return personService.editUser(request, token);
    }

    @PostMapping("/{id}/wall")
    public ResponseRs<PostRs> publishPost(@RequestParam(required = false, name = "publish_date") Long publishDate,
                                          @RequestBody PostRq postRq, @PathVariable(value = "id") int authorId) {

        return postService.publishPost(publishDate, postRq, authorId);
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

    @DeleteMapping("/me")
    public ResponseRs<ComplexRs> deleteUser(@RequestHeader("Authorization") String token) {
        return personService.deleteUser(token);
    }

    @PostMapping("/me/recover")
    public ResponseRs<ComplexRs> publishPost(@RequestHeader("Authorization") String token) {

        return personService.recoverUser(token);
    }
}
