package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Smart Health API")
                        .version("1.0")
                        .description("""
                            # Smart Health API Documentation
                            
                            ## Overview
                            Comprehensive API for Smart Health Application including user management, cycle tracking, health monitoring, and activity logging.
                            
                            ## Features
                            - **User Management**: Registration, authentication, profile management
                            - **Cycle Tracking**: Period tracking, cycle data management
                            - **Health Monitoring**: Weight, water, steps, and activity logging
                            - **Nutrition Tracking**: Food logging with nutritional information
                            - **Goal Management**: Personal health and fitness goals
                            
                            ## Authentication
                            Most endpoints require JWT Bearer token authentication. Include the token in the Authorization header:
                            ```
                            Authorization: Bearer <your-jwt-token>
                            ```
                            
                            ## Response Format
                            Most endpoints return data wrapped in a `data` object:
                            ```json
                            {
                              "data": { ... },
                              "message": "Success message"
                            }
                            ```
                            
                            ## Common Status Codes
                            - `200`: Success
                            - `201`: Created
                            - `400`: Bad Request
                            - `401`: Unauthorized
                            - `403`: Forbidden
                            - `404`: Not Found
                            - `500`: Internal Server Error
                            """)
                        .contact(new Contact()
                                .name("Krishna")
                                .email("krishna@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                    new Server().url("http://localhost:8080").description("Local Development Server"),
                    new Server().url("https://api.smarthealth.com").description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authentication")))
                .tags(List.of(
                    new Tag().name("Authentication").description("User registration, login, and authentication endpoints"),
                    new Tag().name("User Management").description("User profile management and user data operations"),
                    new Tag().name("Cycle Tracking").description("Menstrual cycle tracking and period management"),
                    new Tag().name("Health Logs").description("Weight, water, steps, and activity logging"),
                    new Tag().name("Nutrition").description("Food logging and nutritional tracking"),
                    new Tag().name("Food Entry").description("Food intake tracking and nutritional analysis endpoints"),
                    new Tag().name("Goals").description("Personal health and fitness goal management"),
                    new Tag().name("Activities").description("Exercise and activity management"),
                    new Tag().name("Legacy").description("Legacy endpoints for backward compatibility")
                ));
    }
} 