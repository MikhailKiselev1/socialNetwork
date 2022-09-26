package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.handler.exception.EntityNotFoundException;
import org.javaproteam27.socialnetwork.mapper.CityMapper;
import org.javaproteam27.socialnetwork.model.entity.City;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CityRepository {
    
    private final RowMapper<City> rowMapper = new CityMapper();
    private final JdbcTemplate jdbcTemplate;
    
    
    public City findByTitle(String city) {
        try {
            String sql = "select * from city where title = ?";
            return jdbcTemplate.queryForObject(sql, rowMapper, city);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("city = " + city);
        }
    }
}
