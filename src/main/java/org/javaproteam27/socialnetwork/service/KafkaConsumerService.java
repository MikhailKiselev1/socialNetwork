package org.javaproteam27.socialnetwork.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.javaproteam27.socialnetwork.model.dto.response.NotificationBaseRs;
import org.javaproteam27.socialnetwork.model.entity.Notification;
import org.javaproteam27.socialnetwork.repository.NotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NotificationRepository notificationRepository;

    @KafkaListener(topics = "notifications")
    public void receiveMessage(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {

        Notification notification = objectMapper.readValue(consumerRecord.value(), Notification.class);
        notificationRepository.save(notification);
    }
}
