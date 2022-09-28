package org.javaproteam27.socialnetwork.model.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostLike {
    private Integer id;
    private Long time;
    private Integer personId;
    private Integer postId;
    private String type;
}
