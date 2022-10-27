package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.MessageRq;
import org.javaproteam27.socialnetwork.model.dto.response.*;
import org.javaproteam27.socialnetwork.model.entity.Dialog;
import org.javaproteam27.socialnetwork.model.entity.Message;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.model.enums.ReadStatus;
import org.javaproteam27.socialnetwork.repository.DialogRepository;
import org.javaproteam27.socialnetwork.repository.MessageRepository;
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
    private final PersonService personService;
    private final NotificationService notificationService;

    public ResponseRs<ComplexRs> createDialog(String token, List<Integer> userIds) {

        Integer firstPersonId = personService.getPersonByToken(token).getId();
        Integer secondPersonId = userIds.get(0);
        Dialog dialog = dialogRepository.findByPersonIds(firstPersonId, secondPersonId);
        Integer dialogId;
        if (dialog == null) {
            Dialog newDialog = Dialog.builder()
                    .firstPersonId(firstPersonId)
                    .secondPersonId(secondPersonId)
                    .lastActiveTime(LocalDateTime.now())
                    .build();

            dialogId = dialogRepository.save(newDialog);
        } else {
            dialogId = dialog.getId();
        }
        ComplexRs data = ComplexRs.builder().id(dialogId).build();
        return new ResponseRs<>("", data, null);
    }

    public ListResponseRs<DialogRs> getDialogs(String token, Integer offset, Integer itemPerPage) {

        Person person = personService.getPersonByToken(token);
        Integer personId = person.getId();
        List<Dialog> dialogList = dialogRepository.findByPersonId(personId, offset, itemPerPage);

        List<DialogRs> result = new ArrayList<>();
        if (!dialogList.isEmpty()) {
            for (Dialog dialog : dialogList) {
                Integer unreadCount = messageRepository.countUnreadByDialogIdAndRecipientId(dialog.getId(), personId);
                Integer recipientId = dialog.getFirstPersonId().equals(personId) ?
                        dialog.getSecondPersonId() :
                        dialog.getFirstPersonId();
                if (dialog.getLastMessageId() != 0) {
                    var lastMessage = messageRepository.findById(dialog.getLastMessageId());
                    boolean isSentByMe = (lastMessage.getAuthorId().equals(person.getId()));
                    var recipientPerson = personService.findById(lastMessage.getRecipientId());

                    result.add(DialogRs.builder()
                            .id(dialog.getId())
                            .unreadCount(unreadCount)
                            .lastMessage(buildLastMessageRs(lastMessage, isSentByMe, personService.getPersonRs(recipientPerson)))
                            .authorId(personId)
                            .recipientId(recipientId)
                            .readStatus(lastMessage.getReadStatus())
                            .build());
                } else {
                    var recipientPerson = personService.findById(recipientId);
                    result.add(DialogRs.builder()
                            .id(dialog.getId())
                            .unreadCount(unreadCount)
                            .lastMessage(MessageRs.builder().recipient(personService.getPersonRs(recipientPerson)).build())
                            .authorId(personId)
                            .recipientId(recipientId)
                            .build());
                }
            }
        }
        return new ListResponseRs<>("", offset, itemPerPage, result);
    }

    public ResponseRs<ComplexRs> getUnread(String token) {

        Integer personId = personService.getPersonByToken(token).getId();
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

    public ResponseRs<MessageRs> sendMessage(String token, Integer dialogId, MessageRq text) {

        Integer authorId = personService.getPersonByToken(token).getId();
        Dialog dialog = dialogRepository.findById(dialogId);
        Integer recipientId = dialog.getFirstPersonId().equals(authorId) ?
                dialog.getSecondPersonId() :
                dialog.getFirstPersonId();

        Message message = Message.builder()
                .time(LocalDateTime.now())
                .authorId(authorId)
                .recipientId(recipientId)
                .messageText(text.getMessageText())
                .readStatus(ReadStatus.SENT)
                .dialogId(dialogId)
                .build();

        Integer savedId = messageRepository.save(message);
        message.setId(savedId);

        dialog.setLastMessageId(savedId);
        dialog.setLastActiveTime(LocalDateTime.now());
        dialogRepository.update(dialog);

        MessageRs data = getMessageRs(message);
        data.setIsSentByMe(true);
        notificationService.createMessageNotification(message.getId(), System.currentTimeMillis(), recipientId, token);

        return new ResponseRs<>("", data, null);
    }

    public ListResponseRs<MessageRs> getMessagesByDialog(Integer id, Integer offset,
                                                         Integer itemPerPage, Integer personId) {

        Integer messagesCount = messageRepository.countByDialogId(id);
        List<MessageRs> data = Collections.emptyList();

        if (messagesCount > 0) {

            data = messageRepository.findByDialogId(id, offset, itemPerPage).stream()
                    .map(this::getMessageRs)
                    .collect(Collectors.toList());
            data.forEach(messageRs -> messageRs.setIsSentByMe(messageRs.getAuthorId().equals(personId)));
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

    public ResponseRs<MessageRs> editMessage(Integer messageId, MessageRq text) {

        Message message = messageRepository.findById(messageId);

        message.setMessageText(text.getMessageText());
        message.setReadStatus(ReadStatus.SENT);

        messageRepository.update(message);

        MessageRs data = getMessageRs(message);
        data.setIsSentByMe(true);

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

        MessageRs data = getMessageRs(message);

        return new ResponseRs<>("", data, null);
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

    private MessageRs getMessageRs(Message message) {
        return MessageRs.builder().id(message.getId()).messageText(message.getMessageText())
                .recipientId(message.getRecipientId()).time(Timestamp.valueOf(message.getTime()).getTime())
                .authorId(message.getAuthorId()).readStatus(message.getReadStatus())
                .build();
    }

    public ResponseRs<ComplexRs> markDialogAsReadMessage(Integer dialogId) {

        List<Message> messages = messageRepository.findByDialogId(dialogId, null, null);
        messages.forEach(message -> markAsReadMessage(message.getId()));
        ComplexRs data = ComplexRs.builder().message("ok").build();
        return new ResponseRs<>("", data, null);
    }
}
