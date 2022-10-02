package org.javaproteam27.socialnetwork.model.entity;

import lombok.Builder;
import lombok.Data;
import org.javaproteam27.socialnetwork.model.enums.ReadStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class Message implements Comparable<Message> {
    
    private Integer id;
    private LocalDateTime time;
    private Integer authorId;
    private Integer recipientId;
    private String messageText;
    private ReadStatus readStatus;
    private Integer dialogId;
    private boolean isDeleted;
    
    
    @Override
    public int compareTo(Message o) {
        return time.compareTo(o.time);
    }
}
