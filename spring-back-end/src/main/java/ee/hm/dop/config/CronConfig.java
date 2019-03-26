package ee.hm.dop.config;

import ee.hm.dop.utils.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.inject.Inject;
import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
@EnableAsync
public class CronConfig {

    @Inject
    private ee.hm.dop.config.Configuration configuration;

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(configuration.getInt(ConfigurationProperties.EXECUTOR_corePoolSize));
        executor.setMaxPoolSize(configuration.getInt(ConfigurationProperties.EXECUTOR_maxPoolSize));
        executor.setQueueCapacity(configuration.getInt(ConfigurationProperties.EXECUTOR_queueCapacity));
        executor.setThreadNamePrefix(configuration.getString(ConfigurationProperties.EXECUTOR_threadNamePrefix));
        executor.initialize();
        return executor;
    }
}
