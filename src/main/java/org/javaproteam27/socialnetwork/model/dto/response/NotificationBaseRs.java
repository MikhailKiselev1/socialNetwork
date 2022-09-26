package org.javaproteam27.socialnetwork.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.javaproteam27.socialnetwork.model.enums.NotificationType;

@Data
@Builder
public class NotificationBaseRs {
    
    private Integer id;
    @JsonProperty("notification_type")
    private NotificationType notificationType;
    @JsonProperty("sent_time")
    private Long sentTime;
    @JsonProperty("entity_id")
    private Integer entityId;
    private String info;
    @JsonProperty("entity_author")
    private EntityAuthorRs entityAuthor;
}
