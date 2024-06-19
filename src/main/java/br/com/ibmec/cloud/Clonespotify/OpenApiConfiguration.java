package br.com.ibmec.cloud.Clonespotify;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI apiDocConfig() {
        return new OpenAPI()
                .info(new Info()
                        .title("IBMEC - CLONE-SPOTIFY")
                        .description("CLONE SPOTIFY")
                        .version("1.0")
                        .contact(new Contact()
                                .email("202202102938@alunos.ibmec.edu.br")
                                .name("Raphael Meres")
                        )
                );
    }
}
