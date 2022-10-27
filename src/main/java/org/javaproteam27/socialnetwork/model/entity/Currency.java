package org.javaproteam27.socialnetwork.model.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Currency {
    private Integer id;
    private String name;
    private String price;
}
