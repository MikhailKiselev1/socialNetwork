package org.javaproteam27.socialnetwork.mapper;

import org.javaproteam27.socialnetwork.model.entity.Country;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CountryMapper implements RowMapper<Country> {
    
    @Override
    public Country mapRow(ResultSet rs, int rowNum) throws SQLException {
    
        Country country = new Country();
    
        country.setId(rs.getInt("id"));
        country.setTitle(rs.getString("title"));
    
        return country;
    }
}
