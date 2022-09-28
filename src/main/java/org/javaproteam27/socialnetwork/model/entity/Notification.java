package org.javaproteam27.socialnetwork.model.entity;

import lombok.Builder;
import lombok.Data;
import org.javaproteam27.socialnetwork.model.enums.NotificationType;

@Data
@Builder
public class Notification {
    private Integer id;
    private NotificationType notificationType;
    private Long sentTime;
    private Integer personId;
    private Integer entityId;
    private String contact;
    private boolean isRead;
}
