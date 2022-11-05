package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.aop.DebugLogger;
import org.javaproteam27.socialnetwork.handler.exception.InvalidRequestException;
import org.javaproteam27.socialnetwork.model.dto.request.LoginRq;
import org.javaproteam27.socialnetwork.model.dto.response.*;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.repository.CurrencyRepository;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.javaproteam27.socialnetwork.util.PhotoCloudinary;
import org.javaproteam27.socialnetwork.util.WeatherService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@DebugLogger
public class LoginService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final WeatherService weatherService;
    private final PhotoCloudinary photoCloudinary;
    private final CurrencyRepository currencyRepository;

    public ResponseRs<PersonRs> profileResponse(String token) {
        String email = jwtTokenProvider.getUsername(token);
        Person person = personRepository.findByEmail(email);
        WeatherRs weatherRs;
        if (person.getCity() != null) {
            weatherRs = weatherService.getWeather(person.getCity());
        } else {
            weatherRs = weatherService.getWeather("Москва");
        }
        var currencyUsd = currencyRepository.findByName("USD");
        var currencyEuro = currencyRepository.findByName("EUR");
        PersonRs personRs = getPersonRs(person, token);
        personRs.setDeleted(person.getIsDeleted());
        personRs.setWeather(weatherRs);
        personRs.setCurrency(CurrencyRateRs.builder()
                .usd(currencyUsd.getPrice())
                .euro(currencyEuro.getPrice())
                .build());
        return new ResponseRs<>("string", 0, 20, personRs);
    }

    public ResponseRs<PersonRs> login(LoginRq loginRq) {
        String email = loginRq.getEmail();
        String password = loginRq.getPassword();
        Person person = personRepository.findByEmail(email);
        if (passwordEncoder.matches(password, person.getPassword())) {
            String token = getToken(email);
            PersonRs personRs = getPersonRs(person, token);
            ResponseRs<PersonRs> response = new ResponseRs<>();
            response.setData(personRs);
            response.setError("");
            response.setTimestamp(System.currentTimeMillis());
            return response;
        } else throw new InvalidRequestException("Неверный пароль.");
    }

    private PersonRs getPersonRs(Person person, String token) {
        return PersonRs.builder()
                .id(person.getId())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .regDate(person.getRegDate())
                .birthDate(person.getBirthDate())
                .email(person.getEmail())
                .phone(person.getPhone())
                .photo(photoCloudinary.getUrl(person.getId()))
                .about(person.getAbout())
                .city(person.getCity())
                .country(person.getCountry())
                .messagesPermission(person.getMessagesPermission())
                .lastOnlineTime(person.getLastOnlineTime())
                .isBlocked(person.getIsBlocked())
                .token(token)
                .online(true)
                .build();

    }

    public ResponseRs<ComplexRs> logout() {
        var data = ComplexRs.builder().message("ok").build();
        return new ResponseRs<>("", data, null);
    }


    private String getToken(String email) {
        return jwtTokenProvider.createToken(email);
    }
}
