package com.revoktek.reysol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Permite todas las rutas
                        .allowedOrigins("*") // Permite todos los orígenes
                        .allowedMethods("*") // Permite todos los métodos HTTP
                        .allowedHeaders("*") // Permite todas las cabeceras
                        .allowCredentials(false); // No permite cookies o credenciales
            }
        };
    }
}
