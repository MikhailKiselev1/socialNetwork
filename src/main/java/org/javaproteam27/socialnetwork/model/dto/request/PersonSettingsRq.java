package org.javaproteam27.socialnetwork.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonSettingsRq {
    @JsonProperty("notification_type")
    private String type;
    @JsonProperty("enable")
    private Boolean enable;
}
