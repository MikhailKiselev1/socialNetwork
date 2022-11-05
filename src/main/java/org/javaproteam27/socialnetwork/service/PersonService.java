package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.UserRq;
import org.javaproteam27.socialnetwork.model.dto.response.*;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.model.enums.FriendshipStatusCode;
import org.javaproteam27.socialnetwork.model.enums.MessagesPermission;
import org.javaproteam27.socialnetwork.repository.FriendshipStatusRepository;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.javaproteam27.socialnetwork.security.jwt.JwtUser;
import org.javaproteam27.socialnetwork.util.PhotoCloudinary;
import org.javaproteam27.socialnetwork.util.WeatherService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PhotoCloudinary photoCloudinary;
    private final FriendshipStatusRepository friendshipStatusRepository;
    private final WeatherService weatherService;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Person findById(int id) {
        return personRepository.findById(id);
    }

    public Person findByEmail(String email) {
        return personRepository.findByEmail(email);
    }

    public ListResponseRs<PersonRs> findPerson(String firstName, String lastName, Integer ageFrom, Integer ageTo,
                                               String city, String country, int offset, int itemPerPage) {

        Person authorizedPerson = getAuthorizedPerson();
        List<Person> people = personRepository.findPeople(authorizedPerson, firstName, lastName,
                ageFrom, ageTo, city, country);
        return getResultJson(people, offset, itemPerPage);
    }

    public Person getAuthorizedPerson() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        return personRepository.findByEmail(jwtUser.getUsername());
    }

    private ListResponseRs<PersonRs> getResultJson(List<Person> people, int offset, int itemPerPage) {

        List<PersonRs> data = people.stream()
                .map(person -> PersonRs.builder()
                        .id(person.getId())
                        .firstName(person.getFirstName())
                        .lastName(person.getLastName())
                        .photo(photoCloudinary.getUrl(person.getId()))
                        .birthDate(person.getBirthDate())
                        .about(person.getAbout())
                        .phone(person.getPhone())
                        .lastOnlineTime(person.getLastOnlineTime())
                        .country(person.getCountry())
                        .city(person.getCity())
                        .friendshipStatusCode(getFriendshipStatus(person.getId()))
                        .online(Objects.equals(person.getOnlineStatus(), "ONLINE"))
                        .build())
                .collect(Collectors.toList());


        return new ListResponseRs<>("", offset, itemPerPage, data);
    }

    public PersonRs getPersonRs(Person person) {

        return PersonRs.builder()
                .id(person.getId())
                .email(person.getEmail())
                .phone(person.getPhone())
                .city(person.getCity())
                .country(person.getCountry())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .regDate(person.getRegDate())
                .birthDate(person.getBirthDate())
                .messagesPermission(person.getMessagesPermission())
                .isBlocked(person.getIsBlocked())
                .photo(photoCloudinary.getUrl(person.getId()))
                .weather(getWeather(person))
                .about(person.getAbout())
                .lastOnlineTime(person.getLastOnlineTime())
                .friendshipStatusCode(getFriendshipStatus(person.getId()))
                .online(Objects.equals(person.getOnlineStatus(), "ONLINE"))
                .isDeleted(person.getIsDeleted())
                .build();
    }

    public PersonRs initialize(Integer personId) {

        Person person = findById(personId);
        return getPersonRs(person);
    }

    public UserRs editUser(UserRq request, String token) {

        UserRs response = new UserRs();
        Person person = personRepository.findByEmail(jwtTokenProvider.getUsername(token));
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        String birthDate = request.getBirthDate().split("T")[0];
        LocalDate date = LocalDate.parse(birthDate, formatter);

        person.setBirthDate(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        person.setPhone(request.getPhone());
        person.setAbout(request.getAbout());
        person.setCity(request.getCity());
        person.setCountry(request.getCountry());
        person.setMessagesPermission(request.getMessagesPermission() == null ?
                MessagesPermission.ALL : MessagesPermission.valueOf(request.getMessagesPermission()));
        personRepository.editPerson(person);

        return response;
    }

    public FriendshipStatusCode getFriendshipStatus(Integer dstId) {
        var srcId = getAuthorizedPerson().getId();
        var friendStatus = friendshipStatusRepository.findByPersonId(dstId, srcId);
        if (!friendStatus.isEmpty()) {
            return friendStatus.get(0).getCode();
        } else return FriendshipStatusCode.UNKNOWN;
    }

    public ResponseRs<PersonRs> getUserInfo(int userId) {

        return new ResponseRs<>("", initialize(userId), null);
    }

    public Person getPersonByToken(String token) {
        String email = jwtTokenProvider.getUsername(token);
        return personRepository.findByEmail(email);
    }

    private WeatherRs getWeather(Person person) {
        WeatherRs weatherRs;
        if (person.getCity() != null) {
            weatherRs = weatherService.getWeather(person.getCity());
        } else {
            weatherRs = weatherService.getWeather("Москва");
        }
        return weatherRs;
    }

    public ResponseRs<ComplexRs> deleteUser(String token) {
        String email = jwtTokenProvider.getUsername(token);
        Person person = personRepository.findByEmail(email);
        person.setIsDeleted(true);
        personRepository.setPersonIsDeleted(person);
        var response = new ResponseRs<ComplexRs>();
        response.setData(ComplexRs.builder().message("ok").build());
        response.setError("");
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    //    @Scheduled(fixedDelayString = "PT24H")
//    @Async
    public void fullDeleteUser(Person person) {
        personRepository.findAll().stream().filter(Person::getIsBlocked)
                .filter(person1 -> {
                    LocalDate deletedDate = Instant.ofEpochMilli(person1.getDeletedTime())
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    return deletedDate.isBefore(deletedDate.plusMonths(1));
                }).forEach(personRepository::fullDeletePerson);
    }

    public ResponseRs<ComplexRs> recoverUser(String token) {

        String email = jwtTokenProvider.getUsername(token);
        Person person = personRepository.findByEmail(email);
        person.setIsDeleted(false);
        personRepository.setPersonIsDeleted(person);

        var response = new ResponseRs<ComplexRs>();
        response.setData(ComplexRs.builder().message("ok").build());
        response.setError("");
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}
