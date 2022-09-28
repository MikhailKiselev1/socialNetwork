package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.response.RegisterRs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private String fromEmail = "javaproteams27@yandex.ru";


    public RegisterRs putEmail(String email) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("Subject: Simple Mail");
        message.setText("Ссылка для восстановления eMail: ");

        mailSender.send(message);

        HashMap<String, String> date = new HashMap<>();
        date.put("message", "ok");

        return RegisterRs.builder()
                .error("string")
                .data(date)
                .build();
    }
}
