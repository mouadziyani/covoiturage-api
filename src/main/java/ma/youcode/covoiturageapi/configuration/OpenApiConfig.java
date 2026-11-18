package ma.youcode.covoiturageapi.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class OpenApiConfig {
    @Bean
    public OpenAPI covoiturageOpenApi() {
        return new  OpenAPI()
                .info(new Info()
                        .title("Covoiturage API")
                        .description("Covoiturage API")
                );
    }
}
