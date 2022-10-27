package org.javaproteam27.socialnetwork.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyRateRs {
    private String usd;
    private String euro;
}
