package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.InvalidRequestException;
import org.javaproteam27.socialnetwork.model.dto.request.RegisterRq;
import org.javaproteam27.socialnetwork.model.dto.response.RegisterRs;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.repository.CaptchaRepository;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.repository.PersonSettingsRepository;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final PersonRepository personRepository;
    private final CaptchaRepository captchaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final PersonSettingsRepository personSettingsRepository;
    private String captchaSecret1;
    private String captchaSecret2;
    private String password1;
    private String password2;
    private String email;
    private String firstName;
    private String lastName;

    private String defaultPhoto = "/c55aeb2a-6100-48e6-a006-0cec9f913b38.jpg";


    public RegisterRs postRegister(RegisterRq request) {
        HashMap<String, String> data = new HashMap<>();

        captchaSecret1 = captchaRepository.findByCode(request.getCode()).getSecretCode();
        captchaSecret2 = request.getCodeSecret();
        password1 = request.getPassword1();
        password2 = request.getPassword2();

        // Проверка введенных данных
        if (!checkCaptcha()) {
            throw new InvalidRequestException("Invalid captcha");
        }
        if (!checkPassword()) {
            throw new InvalidRequestException("Password do not match");
        }
        email = request.getEmail();
        if (personRepository.checkEmailExists(email)) {
            throw new InvalidRequestException("This email already exists");
        }
        firstName = request.getFirstName();
        lastName = request.getLastName();

        //Сохранение пользователя в бд
        Person person = new Person();
        person.setEmail(email);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setRegDate(System.currentTimeMillis());
        person.setPassword(passwordEncoder.encode(password1));
        person.setPhoto(defaultPhoto);
        person.setIsApproved(true);  // добавить проверку почты
        person.setLastOnlineTime(System.currentTimeMillis());
        var personId = personRepository.save(person);
        personSettingsRepository.save(personId);
        // ответ успешной регистрации
        data.put("message", "ok");

        return RegisterRs.builder().error("string")
                .timestamp(System.currentTimeMillis())
                .data(data).build();
    }

    private boolean checkPassword() {
        return password1.equals(password2);
    }

    private boolean checkCaptcha() {
        return captchaSecret1.equals(captchaSecret2);
    }

}
