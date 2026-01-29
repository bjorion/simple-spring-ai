package com.example.boardgamebuddy.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "BoardGameBuddy", version = "v1")
)
public class OpenApiConfig {

}

