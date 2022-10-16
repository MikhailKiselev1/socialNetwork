package org.javaproteam27.socialnetwork.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebSocketNotificationRq {
    @JsonProperty("user_id")
    private String userId;
    private String type;
}
