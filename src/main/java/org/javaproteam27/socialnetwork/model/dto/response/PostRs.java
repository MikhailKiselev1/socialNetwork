package org.javaproteam27.socialnetwork.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.util.List;
        /*"id": 6,
      "time": 1649367846506,
      "author" : [],
      "title": "ivan title example2",
      "likes": 0,
      "tags": ["java"],
      "comments": [],
      "type": "POSTED",
      "post_text": "ivan post text example2",
      "is_blocked": false,
      "my_like": false*/
@Data
@Builder
@JsonPropertyOrder({"id", "time", "author", "title", "likes", "tags", "comments",
        "type", "post_text", "is_blocked", "my_like"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRs {
    private Integer id;
    private Long time;
    private PersonRs author;
    private String title;
    private Integer likes;
    private List<String> tags;
    @JsonProperty("comments")
    private List<CommentRs> commentRs;
    private String type;
    @JsonProperty("post_text")
    private String postText;
    @JsonProperty("is_blocked")
    private Boolean isBlocked;
    @JsonProperty("my_like")
    private Boolean myLike;
}