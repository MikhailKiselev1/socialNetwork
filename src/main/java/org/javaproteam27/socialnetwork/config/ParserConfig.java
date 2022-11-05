package org.javaproteam27.socialnetwork.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "parser")
public class ParserConfig {
    private boolean enabled;
}
