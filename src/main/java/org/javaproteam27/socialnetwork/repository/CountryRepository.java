package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.mapper.CountryMapper;
import org.javaproteam27.socialnetwork.model.entity.Country;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CountryRepository {
    
    private final RowMapper<Country> rowMapper = new CountryMapper();
    private final JdbcTemplate jdbcTemplate;
    
    
    public Country findById(int id) {
        try {
            String sql = "select * from country where id = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, id);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("country id = " + id);
        }
    }
}
