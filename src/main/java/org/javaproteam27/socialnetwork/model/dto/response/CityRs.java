package org.javaproteam27.socialnetwork.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.javaproteam27.socialnetwork.model.entity.City;

@Data
@AllArgsConstructor
public class CityRs {

    private int id;
    private String title;

    public CityRs(City city) {
        id = city.getId();
        title = city.getTitle();
    }
}