package com.praxis.authentication.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        
        long startTime = System.currentTimeMillis();
        
        try {
            logRequest(request);
            
            filterChain.doFilter(wrappedRequest, wrappedResponse);
            
            long duration = System.currentTimeMillis() - startTime;
            logResponse(wrappedResponse, duration);
            
            // Si es /oauth2/token, loguear también el body de la petición
            if (request.getRequestURI().contains("/oauth2/token") && "POST".equals(request.getMethod())) {
                logRequestBody(wrappedRequest);
            }
            
            wrappedResponse.copyBodyToResponse();
            
        } catch (Exception e) {
            log.error("❌ Error en la petición: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private void logRequest(HttpServletRequest request) {
        log.info("========================================");
        log.info("📨 NUEVA PETICIÓN RECIBIDA");
        log.info("========================================");
        log.info("Método: {}", request.getMethod());
        log.info("URL: {}", request.getRequestURL());
        log.info("Query String: {}", request.getQueryString());
        log.info("Request URI: {}", request.getRequestURI());
        
        // Parámetros
        log.info("Parámetros:");
        Enumeration<String> paramNames = request.getParameterNames();
        if (paramNames.hasMoreElements()) {
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                String[] paramValues = request.getParameterValues(paramName);
                log.info("  {}: {}", paramName, String.join(", ", paramValues));
            }
        } else {
            log.info("  (sin parámetros)");
        }
        
        // Headers importantes
        log.info("Headers:");
        log.info("  Authorization: {}", maskAuthorization(request.getHeader("Authorization")));
        log.info("  Content-Type: {}", request.getHeader("Content-Type"));
        log.info("  Cookie: {}", request.getHeader("Cookie"));
        
        // Session
        if (request.getSession(false) != null) {
            log.info("Session ID: {}", request.getSession(false).getId());
        } else {
            log.info("Session: no existe");
        }
        
        log.info("========================================");
    }
    
    private void logRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            try {
                String body = new String(content, request.getCharacterEncoding());
                log.info("📦 Body de la petición POST a /oauth2/token:");
                log.info("{}", body);
            } catch (UnsupportedEncodingException e) {
                log.error("Error leyendo body: {}", e.getMessage());
            }
        }
    }
    
    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        log.info("========================================");
        log.info("📤 RESPUESTA ENVIADA");
        log.info("========================================");
        log.info("Status: {}", response.getStatus());
        log.info("Content-Type: {}", response.getContentType());
        log.info("Duración: {} ms", duration);
        log.info("========================================");
    }
    
    private String maskAuthorization(String auth) {
        if (auth == null) return "null";
        if (auth.startsWith("Basic ")) {
            return "Basic [MASKED]";
        }
        if (auth.startsWith("Bearer ")) {
            return "Bearer [MASKED]";
        }
        return auth;
    }
}