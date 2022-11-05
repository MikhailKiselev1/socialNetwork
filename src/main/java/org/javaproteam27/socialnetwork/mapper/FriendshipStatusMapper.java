package org.javaproteam27.socialnetwork.mapper;

import org.javaproteam27.socialnetwork.model.entity.FriendshipStatus;
import org.javaproteam27.socialnetwork.model.enums.FriendshipStatusCode;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendshipStatusMapper implements RowMapper<FriendshipStatus> {
    
    @Override
    public FriendshipStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
    
        FriendshipStatus friendshipStatus = new FriendshipStatus();
    
        friendshipStatus.setId(rs.getInt("id"));
        friendshipStatus.setTime(rs.getTimestamp("time").toLocalDateTime());
        friendshipStatus.setName(rs.getString("name"));
        friendshipStatus.setCode(FriendshipStatusCode.valueOf(rs.getString("code")));
    
        return friendshipStatus;
    }
}
