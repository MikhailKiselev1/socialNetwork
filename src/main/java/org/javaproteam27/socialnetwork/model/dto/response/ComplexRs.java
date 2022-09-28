package org.javaproteam27.socialnetwork.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
public class ComplexRs {
    
    private Integer id;
    private Integer count;
    @JsonProperty("message_id")
    private Integer messageId;
    private String message;
    
}
