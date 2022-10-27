package org.javaproteam27.socialnetwork.service;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.entity.Person;
import org.javaproteam27.socialnetwork.repository.PersonRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final PersonRepository personRepository;

    @EventListener
    public void getSessionSubscribeInfo(SessionSubscribeEvent event) {
        var messageHeaders = event.getMessage().getHeaders();
        var destination = SimpMessageHeaderAccessor.getDestination(messageHeaders);
        var personId = SimpMessageHeaderAccessor.getSubscriptionId(messageHeaders);
        String subscribe = String.format("/user/%s/queue/notifications", personId);
        if (subscribe.equals(destination)) {
            var person = personRepository.findById(Integer.parseInt(personId));
            person.setNotificationsSessionId(SimpMessageHeaderAccessor.getSessionId(messageHeaders));
            person.setOnlineStatus("ONLINE");
            person.setLastOnlineTime(System.currentTimeMillis());
            personRepository.updateNotificationsSessionId(person);
        }
    }

    @EventListener
    public void getSessionDisconnectInfo(SessionDisconnectEvent event) {
        var messageHeaders = event.getMessage().getHeaders();
        var sessionId = SimpMessageHeaderAccessor.getSessionId(messageHeaders);
        var personList = personRepository.findBySessionId(sessionId);
        if (!personList.isEmpty()) {
            for (Person person : personList) {
                person.setLastOnlineTime(System.currentTimeMillis());
                personRepository.deleteSessionId(person);
            }
        }
    }
}
