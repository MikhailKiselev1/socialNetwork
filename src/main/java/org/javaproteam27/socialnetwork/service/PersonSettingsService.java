package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.InvalidRequestException;
import org.javaproteam27.socialnetwork.model.dto.request.PersonSettingsRq;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.PersonSettingsRs;
import org.javaproteam27.socialnetwork.model.dto.response.ResponseRs;
import org.javaproteam27.socialnetwork.model.enums.NotificationType;
import org.javaproteam27.socialnetwork.repository.PersonSettingsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.javaproteam27.socialnetwork.model.enums.NotificationType.*;

@Service
@RequiredArgsConstructor
public class PersonSettingsService {

    private final PersonSettingsRepository personSettingsRepository;
    private final PersonService personService;

    public ListResponseRs<PersonSettingsRs> getPersonSettings() {
        var personId = personService.getAuthorizedPerson().getId();
        var ps = personSettingsRepository.findByPersonId(personId);
        List<PersonSettingsRs> data = new ArrayList<>();
        data.add(convertToResponse(POST, ps.getPostNotification()));
        data.add(convertToResponse(POST_COMMENT, ps.getPostCommentNotification()));
        data.add(convertToResponse(COMMENT_COMMENT, ps.getCommentCommentNotification()));
        data.add(convertToResponse(FRIEND_REQUEST, ps.getFriendRequestNotification()));
        data.add(convertToResponse(MESSAGE, ps.getMessageNotification()));
        data.add(convertToResponse(FRIEND_BIRTHDAY, ps.getFriendBirthdayNotification()));
        data.add(convertToResponse(POST_LIKE, ps.getLikeNotification()));

        return new ListResponseRs<>("", 0, 20, data);
    }

    public ResponseRs<Object> editPersonSettings(PersonSettingsRq rq) {
        var personId = personService.getAuthorizedPerson().getId();
        var ps = personSettingsRepository.findByPersonId(personId);
        switch (rq.getType()) {
            case "POST":
                ps.setPostNotification(rq.getEnable());
                break;
            case "POST_COMMENT":
                ps.setPostCommentNotification(rq.getEnable());
                break;
            case "COMMENT_COMMENT":
                ps.setCommentCommentNotification(rq.getEnable());
                break;
            case "FRIEND_REQUEST":
                ps.setFriendRequestNotification(rq.getEnable());
                break;
            case "MESSAGE":
                ps.setMessageNotification(rq.getEnable());
                break;
            case "FRIEND_BIRTHDAY":
                ps.setFriendBirthdayNotification(rq.getEnable());
                break;
            case "POST_LIKE":
                ps.setLikeNotification(rq.getEnable());
                break;
            default:
                throw new InvalidRequestException("Request with - " + rq.getType() + " not found");
        }
        personSettingsRepository.update(ps);
        HashMap<String, String> data = new HashMap<>();
        data.put("message", "ok");
        return new ResponseRs<>("", data, null);
    }

    private PersonSettingsRs convertToResponse(NotificationType notificationType, boolean enable) {
        return PersonSettingsRs.builder().type(notificationType).enable(enable).build();
    }
}
