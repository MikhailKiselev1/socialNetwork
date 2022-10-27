package org.javaproteam27.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.MessageRq;
import org.javaproteam27.socialnetwork.model.dto.response.DialogUserShortListDto;
import org.javaproteam27.socialnetwork.service.DialogsService;
import org.javaproteam27.socialnetwork.service.PersonService;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class DialogsWebSocketController {

    private final DialogsService dialogsService;

    private final PersonService personService;

    private final SimpMessagingTemplate messagingTemplate;


//    @MessageMapping("/dialogs/chat")
//    @SendTo("/topic/activity")
    /*@MessageMapping("dialogs/send_message")
    public void sendMessage(@Header("token") String token,
            @Header("id_dialog") Integer dialogId,
            @Payload MessageRq messageRq) {

        messagingTemplate.convertAndSendToUser(personService.getPersonByToken(token).getId().toString(),
                "/queue/messages", dialogsService.sendMessage(token, dialogId, messageRq));
    }*/

    @MessageMapping("/dialogs/create_dialog")
    public void createDialog(@Header("token") String token,
                             @Payload DialogUserShortListDto userIds) {

        Map<String, Object> header = new HashMap<>();
        header.put("type", "create_dialog");
        messagingTemplate.convertAndSendToUser(personService.getPersonByToken(token).getId().toString(),
                "/queue/messages", dialogsService.createDialog(token, userIds.getUserIds()), header);
    }

    @MessageMapping("/dialogs/get_dialogs")
    public void getDialog(@Header("token") String token) {

        Map<String, Object> header = new HashMap<>();
        header.put("type", "get_dialogs");
        messagingTemplate.convertAndSendToUser(personService.getPersonByToken(token).getId().toString(),
                "/queue/messages", dialogsService.getDialogs(token, 0, 10), header);
    }

    @MessageMapping("/dialogs/get_unreaded")
    public void getUnread(@Header("token") String token) {

        Map<String, Object> header = new HashMap<>();
        header.put("type", "get_unreaded");
        messagingTemplate.convertAndSendToUser(personService.getPersonByToken(token).getId().toString(),
                "/queue/messages", dialogsService.getUnread(token), header);
    }

    @MessageMapping("/dialogs/delete_dialog")
    public void deleteDialog(@Header("token") String token,
                             @Header("dialog_id") Integer dialogId) {

        Map<String, Object> header = new HashMap<>();
        header.put("type", "get_unreaded");
        messagingTemplate.convertAndSendToUser(personService.getPersonByToken(token).getId().toString(),
                "/queue/messages", dialogsService.deleteDialog(dialogId), header);
    }

    @MessageMapping("/dialogs/send_message")
    public void sendMessage(@Header("token") String token,
                            @Header("dialog_id") Integer dialogId,
                            @Payload MessageRq text) {

        /*Person person = personService.getPersonByToken(token);
        //var listRs = new ListResponseRs<>("", 0, 1, rs);
        if (person.getNotificationsWebsocketUserId() != null) {
            var userId = person.getNotificationsWebsocketUserId();
            messagingTemplate.convertAndSendToUser(userId,
                    "/queue/messages", dialogsService.sendMessage(token, dialogId, text));
        }*/
        Map<String, Object> header = new HashMap<>();
        header.put("type", "send_message");
        messagingTemplate.convertAndSendToUser(personService.getPersonByToken(token).getId().toString(),
                "/queue/messages", dialogsService.sendMessage(token, dialogId, text), header);
    }

    @MessageMapping("/dialogs/get_messages")
    public void getMessages(@Header("token") String token,
                            @Header("dialog_id") Integer dialogId) {

        Map<String, Object> header = new HashMap<>();
        header.put("type", "get_messages");
        messagingTemplate.convertAndSendToUser(personService.getPersonByToken(token).getId().toString(),
                "/queue/messages", dialogsService.getMessagesByDialog(dialogId, 0, 100,
                        personService.getPersonByToken(token).getId()), header);
    }

    @MessageMapping("/dialogs/edit_message")
    public void editMessage(@Header("token") String token,
                            @Header("dialog_id") Integer messageId,
                            @Payload MessageRq text) {

        Map<String, Object> header = new HashMap<>();
        header.put("type", "edit_message");
        messagingTemplate.convertAndSendToUser(personService.getPersonByToken(token).getId().toString(),
                "/queue/messages", dialogsService.editMessage(messageId, text), header);
    }

    @MessageMapping("/dialogs/mark_readed")
    public void markReaded(@Header("token") String token,
                           @Header(value = "id_message", defaultValue = "-1") Integer messageId,
                           @Header(value = "dialog_id") Integer dialogId) {

        Map<String, Object> header = new HashMap<>();
        header.put("type", "mark_readed");
        if (messageId >= 0){
            messagingTemplate.convertAndSendToUser(personService.getPersonByToken(token).getId().toString(),
                    "/queue/messages", dialogsService.markAsReadMessage(messageId), header);

        } else {
            messagingTemplate.convertAndSendToUser(personService.getPersonByToken(token).getId().toString(),
                    "/queue/messages", dialogsService.markDialogAsReadMessage(dialogId), header);
        }
    }

    /*@MessageMapping("/dialogs/mark_readed_all")
    public void markReadedAll(@Header("token") String token,
                           @Header("dialog_id") Integer dialogId) {

        Map<String, Object> header = new HashMap<>();
        header.put("type", "mark_readed_all");

    }*/

    @MessageMapping("/dialogs/delete_message")
    public void deleteMessage(@Header("token") String token,
                           @Header("id_message") Integer messageId,
                           @Header("dialog_id") Integer dialogId) {

        Map<String, Object> header = new HashMap<>();
        header.put("type", "delete_message");
        messagingTemplate.convertAndSendToUser(personService.getPersonByToken(token).getId().toString(),
                "/queue/messages", dialogsService.deleteMessage(dialogId, messageId), header);
    }

    @MessageMapping("/dialogs/recover_message")
    public void recoverMessage(@Header("token") String token,
                              @Header("id_message") Integer messageId,
                              @Header("dialog_id") Integer dialogId) {

        Map<String, Object> header = new HashMap<>();
        header.put("type", "recover_message");
        messagingTemplate.convertAndSendToUser(personService.getPersonByToken(token).getId().toString(),
                "/queue/messages", dialogsService.recoverMessage(dialogId, messageId), header);
    }
}
