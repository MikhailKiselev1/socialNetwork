package org.javaproteam27.socialnetwork.model.entity;

import lombok.*;

@Data
@Builder
public class Post {
    private Integer id;
    private Long time;
    private Integer authorId;
    private String title;
    private String postText;
    private Boolean isBlocked;
    private Boolean isDeleted;
}
