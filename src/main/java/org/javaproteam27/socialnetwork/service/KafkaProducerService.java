package org.javaproteam27.socialnetwork.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javaproteam27.socialnetwork.model.entity.Notification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, Notification> kafkaTemplate;

    public void sendNotificationToQueue(Notification notification) throws JsonProcessingException {

        kafkaTemplate.send("notifications", notification);
    }
}
