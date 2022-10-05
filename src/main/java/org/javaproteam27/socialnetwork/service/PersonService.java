package org.javaproteam27.socialnetwork.service;

import com.dropbox.core.DbxException;
import lombok.RequiredArgsConstructor;

import org.javaproteam27.socialnetwork.model.dto.request.UserRq;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.PersonRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.UserRs;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.model.enums.MessagesPermission;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.javaproteam27.socialnetwork.security.jwt.JwtUser;
import org.javaproteam27.socialnetwork.util.DropBox;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final DropBox dropBox;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Person findById(int id) {
        return personRepository.findById(id);
    }

    public Person findByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    public ListResponseRs<PersonRs> findPerson(String firstName, String lastName, Integer ageFrom, Integer ageTo,
                                               String city, String country, int offset, int itemPerPage) {

        Person authorizedPerson = getAuthorizedPerson();
        List<Person> people = personRepository.findPeople(authorizedPerson, firstName, lastName,
                ageFrom, ageTo, city, country);
        return getResultJson(people, offset, itemPerPage);
    }

    public Person getAuthorizedPerson() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        return personRepository.findByEmail(jwtUser.getUsername());
    }

    private ListResponseRs<PersonRs> getResultJson(List<Person> people, int offset, int itemPerPage) {

        List<PersonRs> data = people.stream()
                .map(person -> PersonRs.builder()
                        .firstName(person.getFirstName())
                        .lastName(person.getLastName())
                        .photo(person.getPhoto())
                        .birthDate(person.getBirthDate())
                        .about(person.getAbout())
                        .phone(person.getPhone())
                        .lastOnlineTime(person.getLastOnlineTime())
                        .country(person.getCountry())
                        .city(person.getCity())
                        .build())
                .collect(Collectors.toList());



        return new ListResponseRs<>("", offset, itemPerPage, data);
    }

    public PersonRs initialize(Integer personId){

        Person person = findById(personId);
        return PersonRs.builder()
                .id(person.getId())
                .email(person.getEmail())
                .phone(person.getPhone())
                .city(person.getCity())
                .country(person.getCountry())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .regDate(person.getRegDate())
                .birthDate(person.getBirthDate())
                .messagesPermission(person.getMessagesPermission())
                .isBlocked(person.getIsBlocked())
                .photo(dropBox.getLinkImages(person.getPhoto()))
                .about(person.getAbout())
                .lastOnlineTime(person.getLastOnlineTime())
                .build();
    }

    public ResponseEntity<UserRs> editUser(UserRq request, String token) {

        UserRs response = new UserRs();
        Person person = personRepository.findByEmail(jwtTokenProvider.getUsername(token));
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        String birthDate = request.getBirthDate().split("T")[0];
        LocalDate date = LocalDate.parse(birthDate, formatter);

        person.setBirthDate(LocalDateTime.of(date, LocalTime.of(0,0,0,0)));
        person.setPhone(request.getPhone());
        person.setAbout(request.getAbout());
        person.setCity(request.getCity());
        person.setCountry(request.getCountry());
        person.setMessagesPermission(request.getMessagesPermission() == null ?
                MessagesPermission.ALL : MessagesPermission.valueOf(request.getMessagesPermission()));
        personRepository.editPerson(person);

        return ResponseEntity.ok(response);
    }

    public ResponseRs<PersonRs> getUserInfo(int userId) {

        return new ResponseRs<>("", initialize(userId), null);
    }
}
