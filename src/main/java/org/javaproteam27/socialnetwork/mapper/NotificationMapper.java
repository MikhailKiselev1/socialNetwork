package org.javaproteam27.socialnetwork.mapper;

import org.javaproteam27.socialnetwork.model.entity.Notification;
import org.javaproteam27.socialnetwork.model.enums.NotificationType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NotificationMapper implements RowMapper<Notification> {
    
    @Override
    public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
    
        Notification notification = new Notification();
    
        notification.setId(rs.getInt("id"));
        notification.setNotificationType(NotificationType.valueOf(rs.getString("notification_type")));
        notification.setSentTime(rs.getTimestamp("sent_time").getTime());
        notification.setPersonId(rs.getInt("person_id"));
        notification.setEntityId(rs.getInt("entity_id"));
        notification.setContact(rs.getString("contact"));
        notification.setRead(rs.getBoolean("is_read"));
    
        return notification;
    }
}
