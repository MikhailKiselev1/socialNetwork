package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.response.ComplexRs;
import org.javaproteam27.socialnetwork.model.dto.response.RegisterRs;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PersonRepository personRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public RegisterRs putPassword(String token, String password) {

        Person person = personRepository.findByEmail(jwtTokenProvider.getUsername(token));
        person.setPassword(passwordEncoder.encode(password));
        personRepository.editPassword(person);

        var data = ComplexRs.builder().message("ok").build();

        return RegisterRs.builder()
                .error("string")
                .data(data)
                .build();
    }
}
