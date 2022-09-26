package org.javaproteam27.socialnetwork.model.entity;

import lombok.Data;
import org.javaproteam27.socialnetwork.model.enums.FriendshipStatusCode;

import java.time.LocalDateTime;

@Data
public class FriendshipStatus {
    
    private int id;
    private LocalDateTime time;
    private String name;
    private FriendshipStatusCode code;
    
}
