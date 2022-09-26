package org.javaproteam27.socialnetwork.model.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Comment {
    private Integer id;
    private Long time;
    private Integer postId;
    private Integer parentId;
    private Integer authorId;
    private String commentText;
    private Boolean isBlocked;
}
