package org.javaproteam27.socialnetwork.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MessageRq {
    
    @JsonProperty("message_text")
    private String messageText;
    
}
