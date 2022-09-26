package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.response.RegisterRs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("spring.mail.username")
    private String username;

    public RegisterRs putEmail(String email) {
        System.out.println("сработал сервис");
        RegisterRs response = new RegisterRs();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("javaproteams27@yandex.ru");
        message.setTo("pks52@mail.ru");
        message.setSubject ("Subject: Simple Mail");
        message.setText ("Ссылка для восстановления пароля:" );

        mailSender.send(message);

        return response;
    }
}
