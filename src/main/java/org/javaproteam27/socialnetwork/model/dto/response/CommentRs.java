package org.javaproteam27.socialnetwork.model.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@Builder
@JsonInclude(NON_NULL)
public class CommentRs {
    private Integer id;
    private Long time;
    @JsonProperty("post_id")
    private Integer postId;
    @JsonProperty("parent_id")
    private Integer parentId;
    @JsonProperty("author")
    private PersonRs author;
    @JsonProperty("comment_text")
    private String commentText;
    @JsonProperty("is_blocked")
    private Boolean isBlocked;
    @JsonProperty("is_deleted")
    private Boolean isDeleted;
    @JsonProperty("sub_comments")
    List<CommentRs> subComments;
    @JsonProperty("likes")
    private Integer subLikes;
    @JsonProperty("my_like")
    private Boolean myLike;
}