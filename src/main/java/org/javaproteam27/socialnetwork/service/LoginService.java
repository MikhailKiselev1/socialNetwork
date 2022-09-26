package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.aop.DebugLogger;
import org.javaproteam27.socialnetwork.handler.exception.InvalidRequestException;
import org.javaproteam27.socialnetwork.model.dto.request.LoginRq;
import org.javaproteam27.socialnetwork.model.dto.response.PersonRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@DebugLogger
public class LoginService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PersonRepository personRepository;

    public ResponseEntity<ResponseRs<PersonRs>> profileResponse(String token) {
        String email = jwtTokenProvider.getUsername(token);
        Person person = personRepository.findByEmail(email);
        PersonRs personRs = getPersonRs(person, token);
        return ResponseEntity.ok(new ResponseRs<>("string", 0, 20, personRs));
    }

    public ResponseRs<PersonRs> login(LoginRq loginRq) {
        String email = loginRq.getEmail();
        String password = loginRq.getPassword();
        Person person = personRepository.findByEmail(email);
        if (person.getPassword().contains(password)) {
            String token = getToken(email);
            PersonRs personRs = getPersonRs(person, token);
            ResponseRs<PersonRs> response = new ResponseRs<>();
            response.setData(personRs);
            response.setError("");
            response.setTimestamp(System.currentTimeMillis());
            return response;
        } else throw new InvalidRequestException("Incorrect password");
    }

    private PersonRs getPersonRs(Person person, String token) {
        return PersonRs.builder().id(person.getId()).firstName(person.getFirstName()).
                lastName(person.getLastName()).regDate(person.getRegDate()).birthDate(person.getBirthDate()).
                email(person.getEmail()).phone(person.getPhone()).photo(person.getPhoto()).about(person.getAbout()).
                city(person.getCity()).country(person.getCountry()).messagesPermission(person.getMessagesPermission()).
                lastOnlineTime(person.getLastOnlineTime()).isBlocked(person.getIsBlocked()).token(token)
                .build();
    }

    public ResponseRs<Object> logout() {
        ResponseRs<Object> response = new ResponseRs<>();
        response.setError("");
        response.setTimestamp(System.currentTimeMillis());
        HashMap<String, String> data = new HashMap<>();
        data.put("message", "ok");
        response.setData(data);
        return response;
    }


    private String getToken(String email) {
        return jwtTokenProvider.createToken(email);
    }
}
