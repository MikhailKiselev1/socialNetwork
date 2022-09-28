package org.javaproteam27.socialnetwork.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageSendRequestBodyRs {
    
    @JsonProperty("message_text")
    private String messageText;
    
}
