package org.javaproteam27.socialnetwork.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class PostRq {
    String title;
    @JsonProperty("post_text")
    String postText;
    List<String> tags;
    @JsonProperty("get_deleted")
    Boolean getDeleted;
}
