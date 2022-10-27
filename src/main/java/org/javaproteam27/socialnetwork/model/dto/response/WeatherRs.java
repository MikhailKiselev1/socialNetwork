package org.javaproteam27.socialnetwork.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeatherRs {
    private String clouds;
    private String temp;
    private String city;
}
