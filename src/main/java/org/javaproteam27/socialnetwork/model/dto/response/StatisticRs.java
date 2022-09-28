package org.javaproteam27.socialnetwork.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticRs {
    @JsonProperty("overall")
    private OverallStatisticRs overallStatisticRs;
    @JsonProperty("personal")
    private PersonalStatisticRs personalStatisticRs;
}
