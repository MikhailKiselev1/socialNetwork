package org.javaproteam27.socialnetwork.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.javaproteam27.socialnetwork.model.enums.MessagesPermission;

@Data
@NoArgsConstructor
public class Person {

    private Integer id;
    private String firstName;
    private String lastName;
    private Long regDate;
    private Long birthDate;
    private String email;
    private String phone;
    private String password;
    private String photo;
    private String about;
    private String city;
    private String country;
    private Integer confirmationCode;
    private Boolean isApproved;
    private MessagesPermission messagesPermission;
    private Long lastOnlineTime;
    private Boolean isBlocked;
    private String token;
    private String changePasswordToken;
    private String notificationsSessionId;
    private String onlineStatus;
    private Boolean isDeleted;
    private Long deletedTime;
}
