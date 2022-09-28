package org.javaproteam27.socialnetwork.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorRs {
    
    private String error;
    @JsonProperty("error_description")
    private String errorDescription;
    
}
