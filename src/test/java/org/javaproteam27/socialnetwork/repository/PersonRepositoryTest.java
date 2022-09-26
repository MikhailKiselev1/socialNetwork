package org.javaproteam27.socialnetwork.repository;

import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.model.enums.MessagesPermission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class PersonRepositoryTest {
    
    @Autowired
    private PersonRepository personRepository;
    
    //@Test
    void save_shouldSavePerson_whenFieldsCorrectAndNotExists() {
        int preSaveCount = 0;
        int expectedCount = 1;
        Person person = getPerson();
    
        personRepository.save(person);
        int postSaveCount = personRepository.count();
        int actualCount = postSaveCount - preSaveCount;
        
        assertEquals(expectedCount, actualCount);
    }
    
    private Person getPerson() {
        Person person = new Person();
    
        person.setId(1);
        person.setFirstName("Alex");
        person.setLastName("Fred");
        person.setRegDate(LocalDateTime.of(2007, 7, 12, 12, 12));
        person.setBirthDate(LocalDateTime.of(2010, 7, 12, 12, 12));
        person.setEmail("qwerty@mail.ru");
        person.setPhone("89999999999");
        person.setPassword("123456");
        person.setPhoto("http://www.photo.com");
        person.setAbout("about");
        person.setCity(null);
        person.setConfirmationCode(123456);
        person.setIsApproved(true);
        person.setMessagesPermission(MessagesPermission.ALL);
        person.setLastOnlineTime(LocalDateTime.of(2012, 7, 12, 12, 12));
        person.setIsBlocked(false);
        
        return person;
    }
    
}
