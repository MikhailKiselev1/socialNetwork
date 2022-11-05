package org.javaproteam27.socialnetwork.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebSocketMessageRq {
    @JsonProperty("dialog_id")
    private Integer dialogId;
    @JsonProperty("message_text")
    private String messageText;
    private String token;
    private String typing;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("author_id")
    private String authorId;
    private Long time;
}
