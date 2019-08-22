package ee.hm.dop.config.cronExecutorsProperties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cron.sitemaps-xmls-generator")
public class GenerateSitemapXmlsExecutorProperties {
    private String scheduledTime;
    private boolean enabled;
}
