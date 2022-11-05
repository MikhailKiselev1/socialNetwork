package org.javaproteam27.socialnetwork.controller;

import org.javaproteam27.socialnetwork.model.dto.response.PersonRs;
import org.javaproteam27.socialnetwork.model.entity.Dialog;
import org.javaproteam27.socialnetwork.model.entity.Message;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.repository.DialogRepository;
import org.javaproteam27.socialnetwork.repository.MessageRepository;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.javaproteam27.socialnetwork.security.jwt.JwtUser;
import org.javaproteam27.socialnetwork.service.KafkaProducerService;
import org.javaproteam27.socialnetwork.service.PersonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql("classpath:sql/person/insert-person.sql")
@Sql({"classpath:sql/dialog/insert-dialog.sql"})
@Transactional
public class DialogsControllerMockTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private DialogRepository dialogRepository;
    @MockBean
    private MessageRepository messageRepository;
    @MockBean
    private KafkaProducerService kafkaProducerService;

    @MockBean
    private PersonService personService;

    private final String url = "/api/v1/dialogs";


    private String getTokenAuthorization() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUser jwtUser = (JwtUser) auth.getPrincipal();
        return jwtTokenProvider.createToken(jwtUser.getUsername());
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void getDialogsWithCorrectRqIsOkResponseWithJsonData() throws Exception {
        var dialog = Dialog.builder().id(1).firstPersonId(1).secondPersonId(2).lastMessageId(1).build();
        var message = Message.builder().id(1).messageText("text").authorId(1).recipientId(2)
                .time(LocalDateTime.now()).build();
        Person person = new Person();
        person.setId(1);

        when(messageRepository.findById(anyInt())).thenReturn(message);
        when(dialogRepository.findByPersonId(anyInt(), anyInt(), anyInt())).thenReturn(List.of(dialog));
        when(personService.getPersonRs(any())).thenReturn(PersonRs.builder().id(2).build());
        when(personService.getPersonByToken(anyString())).thenReturn(person);

        this.mockMvc.perform(get(url).header("Authorization", getTokenAuthorization()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithUserDetails("test@mail.ru")
    public void getMessagesByDialogWithCorrectDataIsOkResponse() throws Exception {
        var message = Message.builder().id(1).messageText("text").authorId(1).recipientId(2).build();
        Person person = new Person();
        person.setId(1);
        when(messageRepository.findByDialogId(anyInt(), anyInt(), anyInt())).thenReturn(List.of(message));
        when(personService.getAuthorizedPerson()).thenReturn(person);

        this.mockMvc.perform(get(url + "/1/messages").header("Authorization", getTokenAuthorization()))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
