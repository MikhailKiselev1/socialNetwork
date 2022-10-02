package org.javaproteam27.socialnetwork.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.javaproteam27.socialnetwork.model.enums.ReadStatus;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageRs {
    
    private Integer id;
    private Long time;
    @JsonProperty("author_id")
    private Integer authorId;
    @JsonProperty("recipient_id")
    private Integer recipientId;
    @JsonProperty("message_text")
    private String messageText;
    @JsonProperty("read_status")
    private ReadStatus readStatus;
    @JsonProperty("recipient")
    private PersonRs recipient;
    private Boolean isSentByMe;
    
}
