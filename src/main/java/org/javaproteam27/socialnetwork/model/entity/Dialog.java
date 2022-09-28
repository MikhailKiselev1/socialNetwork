package org.javaproteam27.socialnetwork.model.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Dialog implements Comparable<Dialog> {
    
    private Integer id;
    private Integer firstPersonId;
    private Integer secondPersonId;
    private Integer lastMessageId;
    private LocalDateTime lastActiveTime;
    
    
    @Override
    public int compareTo(Dialog o) {
        return lastActiveTime.compareTo(o.lastActiveTime);
    }
}
