package org.javaproteam27.socialnetwork.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DialogUserShortListDto {
    
    @JsonProperty("user_ids")
    private List<Integer> userIds;
    private Integer userId;
    
}
