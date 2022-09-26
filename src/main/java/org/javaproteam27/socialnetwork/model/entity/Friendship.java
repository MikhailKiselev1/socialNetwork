package org.javaproteam27.socialnetwork.model.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Friendship {
    
    private int id;
    private int statusId;
    private LocalDateTime sentTime;
    private int srcPersonId;
    private int dstPersonId;
    
}
