package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.mapper.PersonSettingsMapper;
import org.javaproteam27.socialnetwork.model.entity.PersonSettings;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PersonSettingsRepository {

    private final RowMapper<PersonSettings> rowMapper = new PersonSettingsMapper();
    private final JdbcTemplate jdbcTemplate;

    public void save(Integer personId) {
        String sql = "insert into person_settings (person_id) values (?)";
        jdbcTemplate.update(sql, personId);
    }

    public void update(PersonSettings ps) {
        String sql = "update person_settings set post_comment_notification = ?, comment_comment_notification = ?, " +
                "friend_request_notification = ?, message_notification = ?, friend_birthday_notification = ?, " +
                "like_notification = ?, post_notification = ? where person_id = ?";
        jdbcTemplate.update(sql,
                ps.getPostCommentNotification(),
                ps.getCommentCommentNotification(),
                ps.getFriendRequestNotification(),
                ps.getMessageNotification(),
                ps.getFriendBirthdayNotification(),
                ps.getLikeNotification(),
                ps.getPostNotification(),
                ps.getPersonId());
    }

    public PersonSettings findByPersonId(Integer personId) {
        try {
            String sql = "select * from person_settings where person_id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, personId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("person_id = " + personId);
        }
    }
}
