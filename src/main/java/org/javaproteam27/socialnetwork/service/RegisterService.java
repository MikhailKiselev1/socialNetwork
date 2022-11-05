package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.InvalidRequestException;
import org.javaproteam27.socialnetwork.model.dto.request.RegisterRq;
import org.javaproteam27.socialnetwork.model.dto.response.ComplexRs;
import org.javaproteam27.socialnetwork.model.dto.response.RegisterRs;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.repository.CaptchaRepository;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.repository.PersonSettingsRepository;
import org.javaproteam27.socialnetwork.util.PhotoCloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final PersonRepository personRepository;
    private final CaptchaRepository captchaRepository;
    private final PasswordEncoder passwordEncoder;
    private final PersonSettingsRepository personSettingsRepository;
    private final PhotoCloudinary photoCloudinary;
    private String captchaSecret1;
    private String captchaSecret2;
    private String password1;
    private String password2;
    @Value("${register-service.default-photo}")
    private String defaultPhoto;

    public RegisterRs postRegister(RegisterRq request) {

        captchaSecret1 = captchaRepository.findByCode(request.getCode()).getSecretCode();
        captchaSecret2 = request.getCodeSecret();
        password1 = request.getPassword1();
        password2 = request.getPassword2();

        // Проверка введенных данных
        if (!checkCaptcha()) {
            throw new InvalidRequestException("Неврено введена каптча.");
        }
        if (!checkPassword()) {
            throw new InvalidRequestException("Пароли не совпадают.");
        }
        String email = request.getEmail();
        if (personRepository.checkEmailExists(email)) {
            throw new InvalidRequestException("Такой емэйл уже зарегестрирован.");
        }
        String firstName = request.getFirstName();
        String lastName = request.getLastName();

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
        person.setIsDeleted(false);
        var personId = personRepository.save(person);
        photoCloudinary.add(personId, defaultPhoto);
        personSettingsRepository.save(personId);
        // ответ успешной регистрации
        var data = ComplexRs.builder().message("ok").build();

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
