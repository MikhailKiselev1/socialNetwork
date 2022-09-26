package org.javaproteam27.socialnetwork.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
public class FriendshipRs
{
    private String error;
    private LocalDateTime timestamp;
    private HashMap<String,String> data;

}
