package se499.kayaanbackend.AI_Generate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI Configuration for AI Generation feature
 * Provides comprehensive API documentation
 */
@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI aiGenerationOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Generation API")
                        .description("""
                                # AI Generation Service API
                                
                                This API provides comprehensive AI content generation capabilities including:
                                
                                ## Core Features
                                - **AI Content Generation**: Create and manage AI generation requests
                                - **Prompt Templates**: Create, manage, and share prompt templates
                                - **Content Management**: Save, download, and organize generated content
                                - **Real-time Progress**: Track generation progress and status
                                - **Rate Limiting**: Built-in protection against abuse
                                
                                ## Use Cases Covered
                                - UC-19: Create AI Generation Request
                                - UC-20: Start Content Generation
                                - UC-21: Get Generation Status
                                - UC-22: Get User's Generation Requests
                                - UC-23: Cancel Generation
                                - UC-24: Retry Failed Generation
                                - UC-25: Save Generated Content
                                - UC-26: Get User's Saved Content
                                - UC-27: Template Management
                                
                                ## Authentication
                                All endpoints require JWT authentication with appropriate user roles.
                                
                                ## Rate Limiting
                                - **Generation Requests**: 5 per hour per user
                                - **Previews**: 3 per minute per user
                                - **Templates**: 50 per user
                                - **Content**: 100 per user
                                
                                ## Error Handling
                                All endpoints return consistent error responses with appropriate HTTP status codes.
                                
                                ## Support
                                For technical support, contact the development team.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("AI Generation Team")
                                .email("ai-generation@kayaan.com")
                                .url("https://kayaan.com/ai-generation"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.kayaan.com")
                                .description("Production Server"),
                        new Server()
                                .url("https://staging-api.kayaan.com")
                                .description("Staging Server")
                ));
    }
}
