package org.javaproteam27.socialnetwork.mapper;

import org.javaproteam27.socialnetwork.model.entity.Captcha;
import org.javaproteam27.socialnetwork.model.entity.Post;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CaptchaMapper implements RowMapper<Captcha> {
    @Override
    public Captcha mapRow(ResultSet resultSet, int i) throws SQLException {
        return Captcha.builder()
                .id(resultSet.getInt("id"))
                .time(resultSet.getTimestamp("time").toLocalDateTime())
                .code(resultSet.getString("code"))
                .secretCode(resultSet.getString("secret_code"))
                .build();
    }
}
