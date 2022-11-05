package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.EmailRq;
import org.javaproteam27.socialnetwork.model.dto.response.ComplexRs;
import org.javaproteam27.socialnetwork.model.dto.response.RegisterRs;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final JwtTokenProvider jwtTokenProvider;
    private final PersonRepository personRepository;
    @Value("${mailing-service.email}")
    private String fromEmail;
    @Value("${change-url.change-password}")
    private String changePassword;
    @Value("${change-url.change-email}")
    private String changeEmail;

    public RegisterRs putPassword(String token) {


        String email = jwtTokenProvider.getUsername(token);
        Person person = personRepository.findByEmail(email);
        String newToken = UUID.randomUUID().toString();
        person.setChangePasswordToken(newToken);
        personRepository.editPasswordToken(person);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Subject: Simple Mail");
        message.setText("Ссылка для восстановления пароля: " + changePassword + newToken);

        mailSender.send(message);

        var data = ComplexRs.builder().message("ok").build();

        return RegisterRs.builder()
                .error("string")
                .data(data)
                .build();
    }

    public RegisterRs putEmail(String token) {

        String email = jwtTokenProvider.getUsername(token);
        Person person = personRepository.findByEmail(email);
        String newToken = UUID.randomUUID().toString();
        person.setChangePasswordToken(newToken);
        personRepository.editPasswordToken(person);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Subject: Simple Mail");
        message.setText("Ссылка для восстановления email: " + changeEmail + newToken);

        mailSender.send(message);

        var data = ComplexRs.builder().message("ok").build();

        return RegisterRs.builder()
                .error("string")
                .data(data)
                .build();
    }

    public RegisterRs recoverEmail(String token, EmailRq rq) {

        Person person = personRepository.findByEmail(jwtTokenProvider.getUsername(token));
        person.setEmail(rq.getEmail());
        personRepository.updateEmail(person);

        var data = ComplexRs.builder().message("ok").build();

        return RegisterRs.builder()
                .error("string")
                .data(data)
                .build();
    }
}
