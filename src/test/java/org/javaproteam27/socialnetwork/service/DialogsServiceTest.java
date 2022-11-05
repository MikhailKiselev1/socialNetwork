package org.javaproteam27.socialnetwork.service;

import org.javaproteam27.socialnetwork.model.dto.request.MessageRq;
import org.javaproteam27.socialnetwork.model.dto.response.PersonRs;
import org.javaproteam27.socialnetwork.model.entity.Dialog;
import org.javaproteam27.socialnetwork.model.entity.Message;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.model.enums.ReadStatus;
import org.javaproteam27.socialnetwork.repository.DialogRepository;
import org.javaproteam27.socialnetwork.repository.MessageRepository;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class DialogsServiceTest {
    @Mock
    private DialogRepository dialogRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonService personService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private NotificationService notificationService;

    private DialogsService dialogsService;

    @Before
    public void setUp() {
        this.dialogsService = new DialogsService(dialogRepository, messageRepository,
                personService, notificationService);
    }

    @Test
    public void createDialogWithCorrectRqAllDataIsOk() {
        String email = "email";
        Person person = new Person();
        person.setId(1);

        when(jwtTokenProvider.getUsername(anyString())).thenReturn(email);
        when(personRepository.findByEmail(anyString())).thenReturn(person);
//        when(dialogRepository.existsByPersonIds(anyInt(), anyInt())).thenReturn(false);
        when(dialogRepository.findByPersonIds(anyInt(), anyInt())).thenReturn(null);
        when(personService.getPersonByToken(anyString())).thenReturn(person);
        when(dialogRepository.save(any())).thenReturn(1);

        var rq = dialogsService.createDialog("token", List.of(2));
        int dialogId = rq.getData().getId();

        assertNotNull(rq.getData());
        assertEquals(1, dialogId);
        verify(dialogRepository, times(1)).save(any());
    }

    @Test
    public void createDialogWithExistsData() {
        String email = "email";
        Person person = new Person();
        person.setId(1);
        Dialog dialog = Dialog.builder().id(1).firstPersonId(1).secondPersonId(2).build();

        when(jwtTokenProvider.getUsername(anyString())).thenReturn(email);
        when(personRepository.findByEmail(anyString())).thenReturn(person);
//        when(dialogRepository.existsByPersonIds(anyInt(), anyInt())).thenReturn(true);
        when(dialogRepository.findByPersonIds(anyInt(), anyInt())).thenReturn(dialog);
        when(personService.getPersonByToken(anyString())).thenReturn(person);

        var rq = dialogsService.createDialog("token", List.of(2));
        assertNotNull(rq.getData());
    }

    @Test
    public void getDialogsGettingDialogsAllDataIsOk() {
        String email = "email";
        Person person = new Person();
        person.setId(1);
        Person person2 = new Person();
        person2.setId(2);
        person2.setIsBlocked(false);
        Dialog dialog = Dialog.builder().id(1).firstPersonId(1).secondPersonId(2).lastMessageId(1).build();
        Message message = Message.builder().id(1).time(LocalDateTime.now()).messageText("text").authorId(1)
                .recipientId(2).readStatus(ReadStatus.SENT).build();

        when(jwtTokenProvider.getUsername(anyString())).thenReturn(email);
        when(personRepository.findByEmail(anyString())).thenReturn(person);
        when(dialogRepository.findByPersonId(anyInt(), anyInt(), anyInt())).thenReturn(List.of(dialog));
        when(messageRepository.countUnreadByDialogIdAndRecipientId(anyInt(), anyInt())).thenReturn(1);
        when(messageRepository.findById(anyInt())).thenReturn(message);
        when(personRepository.findById(anyInt())).thenReturn(person2);
        when(personService.getPersonByToken(anyString())).thenReturn(person);

        var rq = dialogsService.getDialogs("token", 0, 10);
        int total = rq.getTotal();
        assertNotNull(rq.getData());
        assertEquals(1, total);
    }

    @Test
    public void getDialogsWithLastMessageNullFormingDataWithoutException() {
        String email = "email";
        Person person = new Person();
        person.setId(1);
        Person person2 = new Person();
        person2.setId(2);
        person2.setIsBlocked(false);
        PersonRs personRs = PersonRs.builder().id(2).build();
        Dialog dialog = Dialog.builder().id(1).firstPersonId(1).secondPersonId(2).lastMessageId(0).build();

        when(jwtTokenProvider.getUsername(anyString())).thenReturn(email);
        when(personRepository.findByEmail(anyString())).thenReturn(person);
        when(dialogRepository.findByPersonId(anyInt(), anyInt(), anyInt())).thenReturn(List.of(dialog));
        when(messageRepository.countUnreadByDialogIdAndRecipientId(anyInt(), anyInt())).thenReturn(1);
        when(personRepository.findById(anyInt())).thenReturn(person2);
        when(personService.getPersonByToken(anyString())).thenReturn(person);
        when(personService.getPersonRs(any())).thenReturn(personRs);

        var rq = dialogsService.getDialogs("token", 0, 10);
        int total = rq.getTotal();
        assertEquals(1, total);
        assertNotNull(rq.getData().get(0).getLastMessage().getRecipient());
        assertNull(rq.getData().get(0).getLastMessage().getAuthorId());
    }

    @Test
    public void getUnreadWithCorrectRqAllDataIsOk() {
        String email = "email";
        Person person = new Person();
        person.setId(1);

        when(jwtTokenProvider.getUsername(anyString())).thenReturn(email);
        when(personRepository.findByEmail(anyString())).thenReturn(person);
        when(messageRepository.countUnreadByRecipientId(anyInt())).thenReturn(2);
        when(personService.getPersonByToken(anyString())).thenReturn(person);

        var rq = dialogsService.getUnread("token");
        int count = rq.getData().getCount();
        assertNotNull(rq.getData());
        assertEquals(2, count);
    }

    @Test
    public void deleteDialogWithCorrectRqAllDataIsOk() {
        Dialog dialog = Dialog.builder().id(1).firstPersonId(1).secondPersonId(2).lastMessageId(1).build();

        when(dialogRepository.findById(anyInt())).thenReturn(dialog);

        dialogsService.deleteDialog(1);
        verify(dialogRepository, times(1)).update(any());
        verify(dialogRepository, times(1)).deleteById(anyInt());
        verify(messageRepository, times(1)).deleteByDialogId(anyInt());
    }


    @Test
    public void sendMessageWithCorrectRqAllDataIsOk() {
        String email = "email";
        Person person = new Person();
        person.setId(1);
        Dialog dialog = Dialog.builder().id(1).firstPersonId(1).secondPersonId(2).lastMessageId(1).build();
        MessageRq messageRq = new MessageRq();
        messageRq.setMessageText("text");

        when(jwtTokenProvider.getUsername(anyString())).thenReturn(email);
        when(personRepository.findByEmail(anyString())).thenReturn(person);
        when(dialogRepository.findById(anyInt())).thenReturn(dialog);
        when(personService.getPersonByToken(anyString())).thenReturn(person);

        var rq = dialogsService.sendMessage("token", 1, messageRq);
        int authorId = rq.getData().getAuthorId();
        int recipientId = rq.getData().getRecipientId();

        assertNotNull(rq.getData());
        assertEquals("text", rq.getData().getMessageText());
        assertEquals(1, authorId);
        assertEquals(2, recipientId);
        verify(messageRepository, times(1)).save(any());
        verify(dialogRepository, times(1)).update(any());
        verify(notificationService, times(1))
                .createMessageNotification(anyInt(), anyLong(), anyInt(), anyString());
    }


    @Test
    public void getMessagesByDialogWithCorrectRqAllDataIsOk() {
        Message message = Message.builder().id(1).messageText("text").readStatus(ReadStatus.SENT)
                .authorId(1).recipientId(2).time(LocalDateTime.now()).build();

        when(messageRepository.countByDialogId(anyInt())).thenReturn(1);
        when(messageRepository.findByDialogId(anyInt(), anyInt(), anyInt())).thenReturn(List.of(message));

        var rq = dialogsService.getMessagesByDialog(1, 0, 10, 1);
        int total = rq.getTotal();

        assertNotNull(rq.getData());
        assertEquals("text", rq.getData().get(0).getMessageText());
        assertEquals(1, total);
    }

    @Test
    public void editMessageWithCorrectRqAllDataIsOk() {
        Message message = Message.builder().id(1).messageText("text").readStatus(ReadStatus.SENT)
                .authorId(1).recipientId(2).time(LocalDateTime.now()).build();
        MessageRq messageRq = new MessageRq();
        messageRq.setMessageText("edit text");

        when(messageRepository.findById(anyInt())).thenReturn(message);

        var rq = dialogsService.editMessage(1, messageRq);

        assertNotNull(rq.getData());
        assertEquals("edit text", rq.getData().getMessageText());

        verify(messageRepository, times(1)).update(any());
    }

    @Test
    public void markAsReadMessageWithCorrectRqAllDataIsOk() {
        Message message = Message.builder().id(1).messageText("text").readStatus(ReadStatus.SENT)
                .authorId(1).recipientId(2).time(LocalDateTime.now()).build();

        when(messageRepository.findById(anyInt())).thenReturn(message);

        var rq = dialogsService.markAsReadMessage(1);

        assertNotNull(rq.getData());
        assertEquals("ok", rq.getData().getMessage());

        verify(messageRepository, times(1)).update(any());
    }

    @Test
    public void deleteMessageWithCorrectRqAllDataIsOk() {
        Message message = Message.builder().id(1).messageText("text").readStatus(ReadStatus.SENT)
                .authorId(1).recipientId(2).time(LocalDateTime.now()).build();
        Message message2 = Message.builder().id(2).messageText("text").readStatus(ReadStatus.SENT)
                .authorId(1).recipientId(2).time(LocalDateTime.now()).build();
        Dialog dialog = Dialog.builder().id(1).firstPersonId(1).secondPersonId(2).lastMessageId(1).build();

        when(messageRepository.findById(anyInt())).thenReturn(message);
        when(dialogRepository.findById(anyInt())).thenReturn(dialog);
        when(messageRepository.getLastUndeletedByDialogId(anyInt())).thenReturn(List.of(message2));

        var rq = dialogsService.deleteMessage(1, 1);
        int messageId = rq.getData().getMessageId();

        assertNotNull(rq.getData());
        assertEquals(1, messageId);

        verify(messageRepository, times(1)).update(any());
        verify(dialogRepository, times(1)).update(any());
    }

    @Test
    public void recoverMessageWithCorrectRqAllDataIsOk() {
        Message message = Message.builder().id(1).messageText("text").readStatus(ReadStatus.SENT)
                .authorId(1).recipientId(2).time(LocalDateTime.now()).build();
        Dialog dialog = Dialog.builder().id(1).firstPersonId(1).secondPersonId(2).lastMessageId(1).build();

        when(messageRepository.findById(anyInt())).thenReturn(message);
        when(dialogRepository.findById(anyInt())).thenReturn(dialog);

        var rq = dialogsService.recoverMessage(1, 1);

        assertNotNull(rq.getData());
        assertEquals("text", rq.getData().getMessageText());

        verify(messageRepository, times(1)).update(any());
        verify(dialogRepository, times(1)).update(any());
    }
}
