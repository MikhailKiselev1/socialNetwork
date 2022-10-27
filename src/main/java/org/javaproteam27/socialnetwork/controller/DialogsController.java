package org.javaproteam27.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.request.MessageRq;
import org.javaproteam27.socialnetwork.model.dto.response.*;
import org.javaproteam27.socialnetwork.service.DialogsService;
import org.javaproteam27.socialnetwork.service.PersonService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dialogs")
@RequiredArgsConstructor
public class DialogsController {

    private final DialogsService dialogsService;
    private final PersonService personService;

    @PostMapping
    public ResponseRs<ComplexRs> createDialogs(
            @RequestHeader("Authorization") String token,
            @RequestBody DialogUserShortListDto userIds) {
        return dialogsService.createDialog(token, userIds.getUserIds());
    }

    @GetMapping
    public ListResponseRs<DialogRs> getDialogs(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "perPage", defaultValue = "10") Integer itemPerPage) {
        return dialogsService.getDialogs(token, offset, itemPerPage);
    }

    @GetMapping("/unreaded")
    public ResponseRs<ComplexRs> getUnread(@RequestHeader("Authorization") String token) {
        return dialogsService.getUnread(token);
    }

    @DeleteMapping("/{id}")
    public ResponseRs<ComplexRs> deleteDialog(@PathVariable Integer id) {
        return dialogsService.deleteDialog(id);
    }

    @PostMapping("/{id}/messages")
    public ResponseRs<MessageRs> sendMessage(
            @RequestHeader("Authorization") String token,
            @PathVariable Integer id,
            @RequestBody MessageRq text) {
        return dialogsService.sendMessage(token, id, text);
    }

    @GetMapping("/{id}/messages")
    public ListResponseRs<MessageRs> getMessagesByDialog(
            @PathVariable Integer id,
            @RequestParam(value = "offset", defaultValue = "0") Integer offset,
            @RequestParam(value = "perPage", defaultValue = "10") Integer itemPerPage) {
        return dialogsService.getMessagesByDialog(id, offset, itemPerPage, personService.getAuthorizedPerson().getId());
    }

    @PutMapping("/{dialog_id}/messages/{message_id}")
    public ResponseRs<MessageRs> editMessage(
            @PathVariable("message_id") Integer messageId,
            @RequestBody MessageRq text) {
        return dialogsService.editMessage(messageId, text);
    }

    @PutMapping("/{dialog_id}/messages/{message_id}/read")
    public ResponseRs<ComplexRs> markAsReadMessage(@PathVariable("message_id") Integer messageId) {
        return dialogsService.markAsReadMessage(messageId);
    }

    @PutMapping("/{dialog_id}/read")
    public ResponseRs<ComplexRs> markDialogAsReadMessage(@PathVariable("dialog_id") Integer dialogId) {
        return dialogsService.markDialogAsReadMessage(dialogId);
    }

    @DeleteMapping("/{dialog_id}/messages/{message_id}")
    public ResponseRs<ComplexRs> deleteMessage(
            @PathVariable("dialog_id") Integer dialogId,
            @PathVariable("message_id") Integer messageId) {
        return dialogsService.deleteMessage(dialogId, messageId);
    }

    @PutMapping("/{dialog_id}/messages/{message_id}/recover")
    public ResponseRs<MessageRs> recoverMessage(
            @PathVariable("dialog_id") Integer dialogId,
            @PathVariable("message_id") Integer messageId) {
        return dialogsService.recoverMessage(dialogId, messageId);
    }
}
