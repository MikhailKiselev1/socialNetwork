package org.javaproteam27.socialnetwork.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.javaproteam27.socialnetwork.model.enums.ReadStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class DialogRs {
    
    private Integer id;
    @JsonProperty("unread_count")
    private Integer unreadCount;
    @JsonProperty("last_message")
    private MessageRs lastMessage;
    @JsonProperty("author_id")
    private Integer authorId;
    @JsonProperty("recipient_id")
    private Integer recipientId;
    @JsonProperty("read_status")
    private ReadStatus readStatus;
    
}
