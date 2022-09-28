package org.javaproteam27.socialnetwork.controller;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.model.dto.response.ListResponseRs;
import org.javaproteam27.socialnetwork.model.dto.response.NotificationBaseRs;
import org.javaproteam27.socialnetwork.service.NotificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    
    @GetMapping
    public ListResponseRs<NotificationBaseRs> getNotifications(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "10") int itemPerPage) {

        return notificationService.getNotifications(token, offset, itemPerPage);
    }
    
    @PutMapping
    public ListResponseRs<NotificationBaseRs> markAsReadNotification(
            @RequestHeader("Authorization") String token,
            @RequestParam(value = "id", defaultValue = "0") int id,
            @RequestParam(value = "all", defaultValue = "false") boolean all) {

        return notificationService.markAsReadNotification(token, id, all);
    }
    
}
