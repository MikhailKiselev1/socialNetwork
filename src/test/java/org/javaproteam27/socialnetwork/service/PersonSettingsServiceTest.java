package org.javaproteam27.socialnetwork.service;

import org.javaproteam27.socialnetwork.model.dto.request.PersonSettingsRq;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.model.entity.PersonSettings;
import org.javaproteam27.socialnetwork.model.enums.NotificationType;
import org.javaproteam27.socialnetwork.repository.PersonSettingsRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class PersonSettingsServiceTest {
    @Mock
    private PersonSettingsRepository personSettingsRepository;
    @Mock
    private PersonService personService;

    private PersonSettingsService personSettingsService;

    @Before
    public void setUp() {
        personSettingsService = new PersonSettingsService(personSettingsRepository, personService);
    }

    @Test
    public void getPersonSettingsAuthorizedRqAllDataIsOk() {
        var person = new Person();
        person.setId(1);
        var ps = PersonSettings.builder().personId(1)
                .postNotification(false)
                .postCommentNotification(true)
                .likeNotification(true)
                .messageNotification(true)
                .commentCommentNotification(true)
                .friendBirthdayNotification(true)
                .friendRequestNotification(true)
                .build();

        when(personService.getAuthorizedPerson()).thenReturn(person);
        when(personSettingsRepository.findByPersonId(anyInt())).thenReturn(ps);

        var rs = personSettingsService.getPersonSettings();

        assertNotNull(rs.getData());
        assertEquals(NotificationType.POST, rs.getData().get(0).getType());
        assertEquals(false, rs.getData().get(0).getEnable());
    }

    @Test
    public void editPersonSettingsAuthorizedRqAllDataIsOk() {
        var person = new Person();
        person.setId(1);
        var ps = PersonSettings.builder().personId(1)
                .messageNotification(true)
                .likeNotification(true)
                .build();
        var rq = new PersonSettingsRq();
        rq.setType("POST");
        rq.setEnable(false);

        when(personService.getAuthorizedPerson()).thenReturn(person);
        when(personSettingsRepository.findByPersonId(anyInt())).thenReturn(ps);

        var rs = personSettingsService.editPersonSettings(rq);

        assertNotNull(rs.getData());
        verify(personSettingsRepository, times(1)).update(any());
    }
}
