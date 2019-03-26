package ee.hm.dop.config.cronExecutorsProperties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cron.authentication-state-cleaner")
public class AuthenticationStateCleanerProperties {
    private String scheduledTime;
    private boolean enabled;
}
