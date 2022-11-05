package org.javaproteam27.socialnetwork.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class FriendshipRs
{
    private String error;
    private LocalDateTime timestamp;
    private ComplexRs data;

}
