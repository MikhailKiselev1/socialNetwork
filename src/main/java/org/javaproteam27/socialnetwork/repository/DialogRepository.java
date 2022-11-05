package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.handler.exception.UnableUpdateEntityException;
import org.javaproteam27.socialnetwork.mapper.DialogMapper;
import org.javaproteam27.socialnetwork.model.entity.Dialog;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DialogRepository {
    
    private final DialogMapper rowMapper = new DialogMapper();
    private final JdbcTemplate jdbcTemplate;
    

    public Integer save(Dialog dialog) {

        String sqlQuery = "insert into dialog (first_person_id, second_person_id, last_message_id, last_active_time) " +
                "values (" + dialog.getFirstPersonId() + ", " + dialog.getSecondPersonId() + ", " +
                dialog.getLastMessageId() + ", '" + Timestamp.valueOf(dialog.getLastActiveTime()) + "')";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> connection.prepareStatement(sqlQuery, new String[]{"id"}), keyHolder);
        return (Integer) keyHolder.getKey();
    }
    
    public void update(Dialog dialog) {
    
        String sql = "update dialog set last_message_id = ?, last_active_time = ? where id = ?";
    
        try {
            jdbcTemplate.update(sql, dialog.getLastMessageId(), dialog.getLastActiveTime(), dialog.getId());
        } catch (DataAccessException e) {
            throw new UnableUpdateEntityException("dialog id = " + dialog.getId());
        }
    }
    
    public Dialog findById(Integer id) {
    
        try {
            String sql ="select * from dialog where id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("id = " + id);
        }
    }
    
    public List<Dialog> findByPersonId(Integer id, Integer offset, Integer limit) {
    
        String sql = "select * from dialog where first_person_id = ? or second_person_id = ? " +
                "order by last_active_time desc offset ? limit ?";
        
        try {
            return jdbcTemplate.query(sql, rowMapper, id, id, offset, limit);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("dialogs with person id = " + id);
        }
    }

    public List<Dialog> findByPersonId(Integer id) {

        String sql = "select * from dialog where first_person_id = ? or second_person_id = ? ";

        try {
            return jdbcTemplate.query(sql, rowMapper, id, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("dialogs with person id = " + id);
        }
    }
    
    public Dialog findByPersonIds(Integer firstPersonId, Integer secondPersonId) {
    
//        List<Integer> sortedIds = sortIds(firstPersonId, secondPersonId);
    
//        String sql = "select * from dialog where first_person_id = ? and second_person_id = ?";
        
        try {
            return jdbcTemplate.queryForObject("select * from dialog where first_person_id = ? and second_person_id = ?",
                    rowMapper, firstPersonId, secondPersonId);
        } catch (EmptyResultDataAccessException e) {
            try {
                return jdbcTemplate.queryForObject("select * from dialog where first_person_id = ? and second_person_id = ?",
                        rowMapper, secondPersonId, firstPersonId);
            } catch (EmptyResultDataAccessException e2) {
                return null;
            }
        }
    }

    public void deleteById(Integer id) {
    
        String sql = "delete from dialog where id = ?";
    
        try {
            jdbcTemplate.update(sql, id);
        } catch (DataAccessException e) {
            throw new UnableUpdateEntityException("dialog id = " + id);
        }
    }
    
}
