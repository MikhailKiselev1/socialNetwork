package org.javaproteam27.socialnetwork.model.entity;

import lombok.Data;
import org.javaproteam27.socialnetwork.model.enums.NotificationType;

import java.time.LocalDateTime;

@Data
public class Notification {
    private Integer id;
    private NotificationType notificationType;
    private Long sentTime;
    private Integer personId;
    private Integer entityId;
    private String contact;
    private boolean isRead;
}
