package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.handler.exception.UnableCreateEntityException;
import org.javaproteam27.socialnetwork.handler.exception.UnableUpdateEntityException;
import org.javaproteam27.socialnetwork.mapper.MessageMapper;
import org.javaproteam27.socialnetwork.model.entity.Message;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class MessageRepository {

    private final MessageMapper rowMapper = new MessageMapper();
    private final JdbcTemplate jdbcTemplate;


    public Integer save(Message message) {

        String sql = "insert into message(time, author_id, recipient_id, message_text, read_status, dialog_id)" +
                " values (?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {

                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});

                ps.setTimestamp(1, Timestamp.valueOf(message.getTime()));
                ps.setInt(2, message.getAuthorId());
                ps.setInt(3, message.getRecipientId());
                ps.setString(4, message.getMessageText());
                ps.setString(5, message.getReadStatus().toString());
                ps.setInt(6, message.getDialogId());

                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            throw new UnableCreateEntityException("message with author id = " + message.getAuthorId() +
                    ", recipient id = " + message.getRecipientId() + " with text '" + message.getMessageText() +
                    "' cannot be sent");
        }

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    public void update(Message message) {

        String sql = "update message set time = ?, message_text = ?, read_status = ?, is_deleted = ? where id = ?";

        try {
            jdbcTemplate.update(sql, message.getTime(), message.getMessageText(),
                    message.getReadStatus().toString(), message.isDeleted(), message.getId());
        } catch (DataAccessException e) {
            throw new UnableUpdateEntityException("message id = " + message.getId());
        }
    }

    public Message findById(Integer id) {

        String sql = "select * from message where id = ?";

        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("message id = " + id);
        }
    }

    public List<Message> findByDialogId(Integer dialogId, Integer offset, Integer limit) {

        String sql = "select * from message where dialog_id = ? order by time desc";
        sql = ((offset != null)&&(limit != null)) ? sql + " offset ? limit ?" : sql;

        try {
            return jdbcTemplate.query(sql, rowMapper, dialogId, offset, limit);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("dialogs with person id = " + dialogId);
        }
    }

    public Integer countUnreadByRecipientId(Integer recipientId) {

        String sql = "select count(*) from message where recipient_id = ? and read_status like 'SENT'";
        return jdbcTemplate.queryForObject(sql, Integer.class, recipientId);
    }

    public Integer countByDialogId(Integer dialogId) {

        String sql = "select count(*) from message where dialog_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, dialogId);
    }

    public Integer countUnreadByDialogIdAndRecipientId(Integer dialogId, Integer recipientId) {

        String sql = "select count(*) from message where dialog_id = ? and recipient_id = ? " +
                "and read_status like 'SENT'";
        return jdbcTemplate.queryForObject(sql, Integer.class, dialogId, recipientId);
    }

    public void deleteByDialogId(Integer dialogId) {

        String sql = "delete from message where dialog_id = ?";

        try {
            jdbcTemplate.update(sql, dialogId);
        } catch (DataAccessException e) {
            throw new UnableUpdateEntityException("message with dialog_id = " + dialogId);
        }
    }

    /*public void deleteById(Integer messageId) {

        String sql = "delete from message where id = ?";

        try {
            jdbcTemplate.update(sql, messageId);
        } catch (DataAccessException e) {
            throw new UnableUpdateEntityException("id = " + messageId);
        }
    }*/

    public Integer getPersonalCount(int id) {
        try {
            String sql = "select count(*) from message where author_id = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("id = " + id);
        }
    }

    public List<Message> getLastUndeletedByDialogId(Integer dialogId) {
        String sql = "select * from message where id = " +
                "(select max(id) from message where is_deleted = false and dialog_id = ?)";
        return jdbcTemplate.query(sql, rowMapper, dialogId);
    }
}
