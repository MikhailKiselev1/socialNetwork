package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.handler.exception.UnableUpdateEntityException;
import org.javaproteam27.socialnetwork.mapper.DialogMapper;
import org.javaproteam27.socialnetwork.model.entity.Dialog;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DialogRepository {
    
    private final DialogMapper rowMapper = new DialogMapper();
    private final JdbcTemplate jdbcTemplate;
    
    
    private static List<Integer> sortIds(Integer firstPersonId, Integer secondPersonId) {
        List<Integer> ids = new ArrayList<>();
        ids.add(firstPersonId);
        ids.add(secondPersonId);
        ids.sort(Comparator.naturalOrder());
        return ids;
    }
    
    
    public void save(Dialog dialog) {
        
        Integer firstPersonId = dialog.getFirstPersonId();
        Integer secondPersonId = dialog.getSecondPersonId();
    
        List<Integer> sortedIds = sortIds(firstPersonId, secondPersonId);
        String sql = "insert into dialog (first_person_id, second_person_id, last_message_id, last_active_time)" +
                " values (?,?,?,?)";
        jdbcTemplate.update(sql, sortedIds.get(0), sortedIds.get(1),
                dialog.getLastMessageId(), dialog.getLastActiveTime());
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
    
    public Dialog findByPersonIds(Integer firstPersonId, Integer secondPersonId) {
    
        List<Integer> sortedIds = sortIds(firstPersonId, secondPersonId);
    
        String sql = "select * from dialog where first_person_id = ? and second_person_id = ?";
        
        try {
            return jdbcTemplate.queryForObject(sql, rowMapper, sortedIds.get(0), sortedIds.get(1));
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("dialog with person ids = " + firstPersonId + " and " + secondPersonId);
        }
    }
    
    public Boolean existsByPersonIds(Integer firstPersonId, Integer secondPersonId) {
    
        try {
            return findByPersonIds(firstPersonId, secondPersonId) != null;
        } catch (EntityNotFoundException e) {
            return false;
        }
    }
    
    public Integer countByPersonId(Integer personId) {
        
        String sql = "select count(*) from dialog where first_person_id = ? or second_person_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, personId, personId);
    }
    
    public Integer countByPersonIds(Integer firstPersonId, Integer secondPersonId) {
    
        List<Integer> sortedIds = sortIds(firstPersonId, secondPersonId);
        String sql = "select count(*) from dialog where first_person_id = ? and second_person_id = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, sortedIds.get(0), sortedIds.get(1));
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
