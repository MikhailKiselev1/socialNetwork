package org.javaproteam27.socialnetwork.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DialogUserShortListDto {
    
    @JsonProperty("user_ids")
    private List<Integer> userIds;
    
}
