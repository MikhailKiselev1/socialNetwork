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
import org.springframework.stereotype.Service;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final PersonRepository personRepository;
    private final CaptchaRepository captchaRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private String captchaSecret1;
    private String captchaSecret2;
    private String password1;
    private String password2;

    @Email
    private String email;

    @Pattern(regexp = "[A-Z][a-z]{2,15}|[А-ЯЁ][а-яё]{2,15}", message = "Неверно введено имя")
    private String firstName;

    @Pattern(regexp = "[A-Z][a-z]{2,15}|[А-ЯЁ][а-яё]{2,15}", message = "Неверно введена Фамилия")
    private String lastName;

    private String defaultPhoto = "https://dl.dropbox.com/s/ea3n2vw79u0s33u/default.jpg?dl=1";


    public ResponseEntity<RegisterRs> postRegister(RegisterRq request) {
        RegisterRs registerRS = new RegisterRs();
        HashMap<String, String> data = new HashMap<>();

        captchaSecret1 = captchaRepository.findByCode(request.getCode()).getSecretCode();
        captchaSecret2 = request.getCodeSecret();
        password1 = request.getPassword1();
        password2 = request.getPassword2();

        // Проверка введенных данных
        checkPassword();
        if (!checkCaptcha()) {
            registerRS.setError("invalid_captcha");
            registerRS.setErrorDescription("невверно введена капча");
            return new ResponseEntity<>(registerRS, HttpStatus.BAD_REQUEST);
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
        person.setPassword(request.getPassword1());
        person.setPhoto(defaultPhoto);
        person.setIsApproved(true);  // добавить проверку почты
        personRepository.save(person);
        // ответ успешной регистрации
        registerRS.setError("string");
        registerRS.setTimestamp(System.currentTimeMillis());
        data.put( "message","ok");
        registerRS.setData(data);

        return new ResponseEntity<>(registerRS, HttpStatus.OK);
    }

    @AssertTrue(message = "Пароли не совпадают")
    private boolean checkPassword() {
        return password1.equals(password2);
    }

    @AssertTrue(message = "Неверно введен код с картинки")
    private boolean checkCaptcha() {
        return captchaSecret1.equals(captchaSecret2);
    }

}
