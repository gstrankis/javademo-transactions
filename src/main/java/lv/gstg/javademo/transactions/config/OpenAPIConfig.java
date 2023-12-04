package lv.gstg.javademo.transactions.config;


import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.SneakyThrows;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(type = SecuritySchemeType.DEFAULT)
public class OpenAPIConfig {

    @Bean
    @SneakyThrows
    public OpenAPI openAPI(BuildProperties buildProperties) {
        return new OpenAPI()
                .info(new Info()
                        .title("Transactions Demo")
                        .version(buildProperties.getVersion()));
    }

}
