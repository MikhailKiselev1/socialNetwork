package org.javaproteam27.socialnetwork.model.entity;

import lombok.Data;

@Data
public class PostLike {
    private Integer id;
    private Long time;
    private Integer personId;
    private Integer postId;
    private String type;
}
