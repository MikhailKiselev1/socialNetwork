package org.javaproteam27.socialnetwork.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebSocketMessageRq {
//    private String text;
    @JsonProperty("dialog_id")
    private Integer dialogId;
    @JsonProperty("message_text")
    private String messageText;
    private String token;
}
