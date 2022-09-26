package org.javaproteam27.socialnetwork.mapper;

import org.javaproteam27.socialnetwork.model.entity.Post;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PostMapper implements RowMapper<Post> {
    @Override
    public Post mapRow(ResultSet resultSet, int i) throws SQLException {
        return Post.builder()
                .id(resultSet.getInt("id"))
                .time(resultSet.getTimestamp("time").getTime())
                .authorId(resultSet.getInt("author_id"))
                .title(resultSet.getString("title"))
                .postText(resultSet.getString("post_text"))
                .isBlocked(resultSet.getBoolean("is_blocked"))
                .build();
    }
}