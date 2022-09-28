package org.javaproteam27.socialnetwork.mapper;

import org.javaproteam27.socialnetwork.model.entity.Dialog;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DialogMapper implements RowMapper<Dialog> {
    
    @Override
    public Dialog mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        return Dialog.builder()
                .id(rs.getInt("id"))
                .firstPersonId(rs.getInt("first_person_id"))
                .secondPersonId(rs.getInt("second_person_id"))
                .lastMessageId(rs.getInt("last_message_id"))
                .lastActiveTime(rs.getTimestamp("last_active_time").toLocalDateTime())
                .build();
    }
}
