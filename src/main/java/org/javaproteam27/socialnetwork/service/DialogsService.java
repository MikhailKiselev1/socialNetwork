package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.UnableCreateEntityException;
import org.javaproteam27.socialnetwork.model.dto.response.ComplexRs;
import org.javaproteam27.socialnetwork.model.dto.response.DialogRs;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.MessageRs;
import org.javaproteam27.socialnetwork.model.dto.response.MessageSendRequestBodyRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
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
        
        if (dialogRepository.existsByPersonIds(firstPersonId, secondPersonId)) {
            throw new UnableCreateEntityException("dialog with person ids = " + firstPersonId +
                    " and " + secondPersonId + " already exists");
        }
        
        Dialog newDialog = Dialog.builder()
                .firstPersonId(getPerson(token).getId())
                .secondPersonId(userIds.get(0))
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
    
        Integer dialogsCount = dialogRepository.countByPersonId(personId);
        List<DialogRs> data = Collections.emptyList();
        
        if (dialogsCount > 0) {
            List<Dialog> dialogs = dialogRepository.findByPersonId(personId, offset, itemPerPage);
            data = dialogs.stream()
                    .map(dialog -> {
                        MessageRs messageRs = null;
                        Integer lastMessageId = dialog.getLastMessageId();
                
                        if (lastMessageId != null && lastMessageId != 0) {
                            Message message = messageRepository.findById(lastMessageId);
                            messageRs = buildMessageRs(message);
                        }
                
                        return DialogRs.builder()
                                .id(dialog.getId())
                                .unreadCount(messageRepository.countUnreadedByDialogId(dialog.getId()))
                                .lastMessage(messageRs)
                                .build();
                    })
                    .collect(Collectors.toList());
        }
        
        return ListResponseRs.<DialogRs>builder()
                .error("")
                .timestamp(System.currentTimeMillis())
                .total(dialogsCount)
                .offset(offset)
                .perPage(itemPerPage)
                .data(data)
                .build();
    }
    
    public ResponseRs<ComplexRs> getUnreaded(String token) {
        
        Integer personId = getPerson(token).getId();
        Integer unreadedCount = messageRepository.countUnreadedByRecipientId(personId);
        ComplexRs data = ComplexRs.builder().count(unreadedCount).build();
        
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
    
    public ResponseRs<MessageRs> sendMessage(String token, Integer dialogId, MessageSendRequestBodyRs text) {
        
        Integer authorId = getPerson(token).getId();
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
        
        MessageRs data = buildMessageRs(message);
        notificationService.createMessageNotification(savedId, System.currentTimeMillis(), recipientId);
        
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
        
        return  ListResponseRs.<MessageRs>builder()
                .error("")
                .timestamp(System.currentTimeMillis())
                .total(messagesCount)
                .offset(offset)
                .perPage(itemPerPage)
                .data(data)
                .build();
    }
    
    public ResponseRs<MessageRs> editMessage(Integer dialogId, Integer messageId, MessageSendRequestBodyRs text) {
    
        Message message = messageRepository.findById(messageId);
        
        message.setMessageText(text.getMessageText());
        message.setReadStatus(ReadStatus.SENT);
        
        messageRepository.update(message);
    
        MessageRs data = buildMessageRs(message);
    
        return new ResponseRs<>("", data, null);
    }
    
    public ResponseRs<ComplexRs> markAsReadMessage(Integer dialogId, Integer messageId) {
    
        Message message = messageRepository.findById(messageId);
        
        message.setReadStatus(ReadStatus.READ);
    
        messageRepository.update(message);
        
        ComplexRs data = ComplexRs.builder().message("ok").build();
    
        return new ResponseRs<>("", data, null);
    }
    
    public ResponseRs<ComplexRs> deleteMessage(Integer dialogId, Integer messageId) {
    
        Message message = messageRepository.findById(messageId);
        Dialog dialog = dialogRepository.findById(message.getDialogId());
        List<Message> messageList = messageRepository.findByDialogId(dialog.getId(), 2, 1);
        
        if (messageList.isEmpty()) {
            dialog.setLastMessageId(null);
        } else {
            dialog.setLastMessageId(messageList.get(0).getId());
        }
        
        dialogRepository.update(dialog);
        messageRepository.deleteById(messageId);
        
        ComplexRs data = ComplexRs.builder().messageId(messageId).build();
    
        return new ResponseRs<>("", data, null);
    }
}
