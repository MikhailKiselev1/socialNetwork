package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.RegisterRq;
import org.javaproteam27.socialnetwork.model.dto.response.RegisterRs;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.repository.CaptchaRepository;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final PersonRepository personRepository;
    private final CaptchaRepository captchaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private String captchaSecret1;
    private String captchaSecret2;
    private String password1;
    private String password2;
    private String email;
    private String firstName;
    private String lastName;

    private String defaultPhoto = "/c55aeb2a-6100-48e6-a006-0cec9f913b38.jpg";


    public ResponseEntity postRegister(RegisterRq request) {
        HashMap<String, String> data = new HashMap<>();

        captchaSecret1 = captchaRepository.findByCode(request.getCode()).getSecretCode();
        captchaSecret2 = request.getCodeSecret();
        password1 = request.getPassword1();
        password2 = request.getPassword2();

        // Проверка введенных данных
        checkPassword();
        if (!checkCaptcha()) {
            return new  ResponseEntity(RegisterRs.builder().error("invalid_captcha")
                    .errorDescription("невверно введена капча").build(), HttpStatus.BAD_REQUEST);
        }
        email = request.getEmail();
        firstName = request.getFirstName();
        lastName = request.getLastName();

        //Сохранение пользователя в бд
        Person person = new Person();
        person.setEmail(email);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setRegDate(LocalDateTime.now());
        person.setPassword(passwordEncoder.encode(password1));
        person.setPhoto(defaultPhoto);
        person.setIsApproved(true);  // добавить проверку почты
        personRepository.save(person);
        // ответ успешной регистрации
        data.put( "message","ok");

        return new ResponseEntity<>(RegisterRs.builder().error("string")
                .timestamp(System.currentTimeMillis())
                .data(data).build(), HttpStatus.OK);
    }

    private boolean checkPassword() {
        return password1.equals(password2);
    }

    private boolean checkCaptcha() {
        return captchaSecret1.equals(captchaSecret2);
    }

}
