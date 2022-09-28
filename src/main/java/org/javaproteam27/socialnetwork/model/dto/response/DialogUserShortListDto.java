package org.javaproteam27.socialnetwork.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DialogUserShortListDto {
    
    @JsonProperty("users_ids")
    private List<Integer> userIds;
    
}
