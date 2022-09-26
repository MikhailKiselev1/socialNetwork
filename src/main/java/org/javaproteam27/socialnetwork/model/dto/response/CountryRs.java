package org.javaproteam27.socialnetwork.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.javaproteam27.socialnetwork.model.entity.Country;

@Data
@AllArgsConstructor
public class CountryRs {

    private int id;
    private String title;

    public CountryRs(Country country) {
        this.id = country.getId();
        this.title = country.getTitle();
    }
}