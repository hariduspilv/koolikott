package ee.hm.dop.config.cronExecutorsProperties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cron.material-sync")
public class MaterialSyncProperties {
    private String scheduledTime;
    private boolean enabled;
}
