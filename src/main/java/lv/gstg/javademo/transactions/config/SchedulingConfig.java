package lv.gstg.javademo.transactions.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(
        value = "app.scheduling.enabled", havingValue = "true", matchIfMissing = false
)
public class SchedulingConfig {

}
