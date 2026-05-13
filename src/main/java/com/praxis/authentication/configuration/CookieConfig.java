package com.praxis.authentication.configuration;

import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.SessionTrackingMode;
import java.util.EnumSet;

@Configuration
public class CookieConfig {

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        return servletContext -> {
            // Forzar uso de cookies SOLO para session tracking
            servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));
        };
    }
}
