package lv.gstg.javademo.transactions.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "lv.gstg.javademo.transactions")
@EnableTransactionManagement
@EnableRetry
public class DatabaseConfig {

}
