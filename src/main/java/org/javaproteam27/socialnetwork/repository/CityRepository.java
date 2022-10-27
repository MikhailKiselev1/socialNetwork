package org.javaproteam27.socialnetwork.repository;

import lombok.RequiredArgsConstructor;
import org.javaproteam27.socialnetwork.mapper.CityMapper;
import org.javaproteam27.socialnetwork.model.entity.City;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CityRepository {

    private final RowMapper<City> rowMapper = new CityMapper();
    private final JdbcTemplate jdbcTemplate;


    public List<City> findByTitle(String city) {
        String sql = "select * from city where title = ?";
        return jdbcTemplate.query(sql, rowMapper, city);
    }

    public void saveOrUpdate(City city) {
        String sql = "insert into city (title, country_id, temp, clouds) values (?, ?, ?, ?) " +
                "on conflict (title) do update set temp = ?, clouds = ?";
        jdbcTemplate.update(sql, city.getTitle(), city.getCountryId(), city.getTemp(), city.getClouds(),
                city.getTemp(), city.getClouds());
    }
}
