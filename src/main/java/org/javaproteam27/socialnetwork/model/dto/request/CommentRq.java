package org.javaproteam27.socialnetwork.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@JsonInclude(NON_NULL)
public class CommentRq {
    @JsonProperty("parent_id")
    Integer parentId;
    @JsonProperty("comment_text")
    //@JsonProperty(defaultValue = null)
    String commentText;
    @JsonProperty("get_deleted")
    Boolean getDeleted;
}