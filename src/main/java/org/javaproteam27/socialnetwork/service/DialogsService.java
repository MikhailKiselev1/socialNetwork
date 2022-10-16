package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.UnableCreateEntityException;
import org.javaproteam27.socialnetwork.model.dto.request.WebSocketMessageRq;
import org.javaproteam27.socialnetwork.model.dto.response.*;
import org.javaproteam27.socialnetwork.model.entity.Dialog;
import org.javaproteam27.socialnetwork.model.entity.Message;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.model.enums.ReadStatus;
import org.javaproteam27.socialnetwork.repository.DialogRepository;
import org.javaproteam27.socialnetwork.repository.MessageRepository;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.javaproteam27.socialnetwork.security.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DialogsService {

    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final PersonRepository personRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final NotificationService notificationService;

    private static MessageRs buildMessageRs(Message message) {
        return MessageRs.builder()
                .id(message.getId())
                .time(Timestamp.valueOf(message.getTime()).getTime())
                .authorId(message.getAuthorId())
                .recipientId(message.getRecipientId())
                .messageText(message.getMessageText())
                .readStatus(message.getReadStatus())
                .build();
    }


    private Person getPerson(String token) {
        String email = jwtTokenProvider.getUsername(token);
        return personRepository.findByEmail(email);
    }


    public ResponseRs<ComplexRs> createDialog(String token, List<Integer> userIds) {

        Integer firstPersonId = getPerson(token).getId();
        Integer secondPersonId = userIds.get(0);

        if (Boolean.TRUE.equals(dialogRepository.existsByPersonIds(firstPersonId, secondPersonId))) {
            throw new UnableCreateEntityException("dialog with person ids = " + firstPersonId +
                    " and " + secondPersonId + " already exists");
        }

        Dialog newDialog = Dialog.builder()
                .firstPersonId(firstPersonId)
                .secondPersonId(secondPersonId)
                .lastActiveTime(LocalDateTime.now())
                .build();

        dialogRepository.save(newDialog);
        Dialog dialog = dialogRepository.findByPersonIds(firstPersonId, secondPersonId);
        ComplexRs data = ComplexRs.builder().id(dialog.getId()).build();

        return new ResponseRs<>(null, data, null);
    }

    public ListResponseRs<DialogRs> getDialogs(String token, Integer offset, Integer itemPerPage) {

        Person person = getPerson(token);
        Integer personId = person.getId();
        List<Dialog> dialogList = dialogRepository.findByPersonId(personId, offset, itemPerPage);

        List<DialogRs> result = new ArrayList<>();
        if (!dialogList.isEmpty()) {
            for (Dialog dialog : dialogList) {
                Integer unreadCount = messageRepository.countUnreadByDialogId(dialog.getId());
                Integer recipientId = dialog.getFirstPersonId().equals(personId) ?
                        dialog.getSecondPersonId() :
                        dialog.getFirstPersonId();
                if (dialog.getLastMessageId() != 0) {
                    var lastMessage = messageRepository.findById(dialog.getLastMessageId());
                    boolean isSentByMe = getSentMessageAuthor(lastMessage, person);
                    var recipientPerson = personRepository.findById(lastMessage.getRecipientId());

                    result.add(DialogRs.builder()
                            .id(dialog.getId())
                            .unreadCount(unreadCount)
                            .lastMessage(buildLastMessageRs(lastMessage, isSentByMe, buildRecipientPerson(recipientPerson)))
                            .authorId(personId)
                            .recipientId(recipientId)
                            .readStatus(lastMessage.getReadStatus())
                            .build());
                } else {
                    var recipientPerson = personRepository.findById(recipientId);
                    result.add(DialogRs.builder()
                            .id(dialog.getId())
                            .unreadCount(unreadCount)
                            .lastMessage(MessageRs.builder().recipient(buildRecipientPerson(recipientPerson)).build())
                            .authorId(personId)
                            .recipientId(recipientId)
                            .build());
                }
            }
        }
        return new ListResponseRs<>("", offset, itemPerPage, result);
    }

    public ResponseRs<ComplexRs> getUnread(String token) {

        Integer personId = getPerson(token).getId();
        Integer unreadCount = messageRepository.countUnreadByRecipientId(personId);
        ComplexRs data = ComplexRs.builder().count(unreadCount).build();

        return new ResponseRs<>(null, data, null);
    }

    public ResponseRs<ComplexRs> deleteDialog(Integer dialogId) {

        Dialog dialog = dialogRepository.findById(dialogId);
        dialog.setLastMessageId(null);
        dialogRepository.update(dialog);

        messageRepository.deleteByDialogId(dialogId);
        dialogRepository.deleteById(dialogId);

        ComplexRs data = ComplexRs.builder().id(dialogId).build();

        return new ResponseRs<>("", data, null);
    }

    public ResponseRs<MessageRs> sendMessage(WebSocketMessageRq messageRq) {
        var token = messageRq.getToken();
        Person person = getPerson(token);
        Integer authorId = person.getId();
        Dialog dialog = dialogRepository.findById(messageRq.getDialogId());
        Integer recipientId = dialog.getFirstPersonId().equals(authorId) ?
                dialog.getSecondPersonId() :
                dialog.getFirstPersonId();

        Message message = Message.builder()
                .time(LocalDateTime.now())
                .authorId(authorId)
                .recipientId(recipientId)
                .messageText(messageRq.getMessageText())
                .readStatus(ReadStatus.SENT)
                .dialogId(messageRq.getDialogId())
                .build();

        Integer savedId = messageRepository.save(message);
        message.setId(savedId);

        dialog.setLastMessageId(savedId);
        dialog.setLastActiveTime(LocalDateTime.now());
        dialogRepository.update(dialog);

        MessageRs data = buildMessageRs(message);
        notificationService.createMessageNotification(savedId, System.currentTimeMillis(), recipientId, token);

        return new ResponseRs<>("", data, null);
    }

    public ListResponseRs<MessageRs> getMessagesByDialog(Integer id, Integer offset, Integer itemPerPage) {

        Integer messagesCount = messageRepository.countByDialogId(id);
        List<MessageRs> data = Collections.emptyList();

        if (messagesCount > 0) {
            data = messageRepository.findByDialogId(id, offset, itemPerPage).stream()
                    .map(DialogsService::buildMessageRs)
                    .collect(Collectors.toList());
        }

        return ListResponseRs.<MessageRs>builder()
                .error("")
                .timestamp(System.currentTimeMillis())
                .total(messagesCount)
                .offset(offset)
                .perPage(itemPerPage)
                .data(data)
                .build();
    }

    public ResponseRs<MessageRs> editMessage(Integer messageId, MessageSendRequestBodyRs text) {

        Message message = messageRepository.findById(messageId);

        message.setMessageText(text.getMessageText());
        message.setReadStatus(ReadStatus.SENT);

        messageRepository.update(message);

        MessageRs data = buildMessageRs(message);

        return new ResponseRs<>("", data, null);
    }

    public ResponseRs<ComplexRs> markAsReadMessage(Integer messageId) {

        Message message = messageRepository.findById(messageId);
        message.setReadStatus(ReadStatus.READ);

        messageRepository.update(message);

        ComplexRs data = ComplexRs.builder().message("ok").build();

        return new ResponseRs<>("", data, null);
    }

    public ResponseRs<ComplexRs> deleteMessage(Integer dialogId, Integer messageId) {

        Message message = messageRepository.findById(messageId);
        message.setDeleted(true);
        messageRepository.update(message);

        Dialog dialog = dialogRepository.findById(dialogId);
        List<Message> lastMessage = messageRepository.getLastUndeletedByDialogId(dialogId);
        if (!lastMessage.isEmpty()) {
            dialog.setLastMessageId(lastMessage.get(0).getId());
        } else dialog.setLastMessageId(null);
        dialogRepository.update(dialog);


        ComplexRs data = ComplexRs.builder().messageId(messageId).build();

        return new ResponseRs<>("", data, null);
    }

    public ResponseRs<MessageRs> recoverMessage(Integer dialogId, Integer messageId) {
        var message = messageRepository.findById(messageId);
        message.setDeleted(false);
        messageRepository.update(message);

        var dialog = dialogRepository.findById(dialogId);
        dialog.setLastMessageId(message.getId());
        dialogRepository.update(dialog);

        MessageRs data = buildMessageRs(message);

        return new ResponseRs<>("", data, null);
    }

    private boolean getSentMessageAuthor(Message message, Person person) {
        return message.getAuthorId() == person.getId();
    }

    private PersonRs buildRecipientPerson(Person recipientPerson) {
        return PersonRs.builder()
                .id(recipientPerson.getId())
                .photo(recipientPerson.getPhoto())
                .firstName(recipientPerson.getFirstName())
                .lastName(recipientPerson.getLastName())
                .isBlocked(recipientPerson.getIsBlocked())
                .build();
    }

    private MessageRs buildLastMessageRs(Message lastMessage, boolean isSentByMe, PersonRs recipientPersonRs) {
        return MessageRs.builder()
                .id(lastMessage.getId())
                .time(Timestamp.valueOf(lastMessage.getTime()).getTime())
                .isSentByMe(isSentByMe)
                .recipient(recipientPersonRs)
                .messageText(lastMessage.getMessageText())
                .build();
    }
}
