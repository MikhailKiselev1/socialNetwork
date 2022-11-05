package org.javaproteam27.socialnetwork.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
@Builder
public class RegisterRs {

    private String error;
    private String email;
    private long timestamp;
    private ComplexRs data;
    @JsonProperty("error_description")
    private String errorDescription;
}
