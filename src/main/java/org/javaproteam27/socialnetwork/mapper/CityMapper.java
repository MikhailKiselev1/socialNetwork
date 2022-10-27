package org.javaproteam27.socialnetwork.mapper;

import org.javaproteam27.socialnetwork.model.entity.City;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CityMapper implements RowMapper<City> {
    
    @Override
    public City mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        City city = new City();
    
        city.setId(rs.getInt("id"));
        city.setTitle(rs.getString("title"));
        city.setCountryId(rs.getInt("country_id"));
        city.setClouds(rs.getString("clouds"));
        city.setTemp(rs.getString("temp"));
    
        return city;
    }
}
