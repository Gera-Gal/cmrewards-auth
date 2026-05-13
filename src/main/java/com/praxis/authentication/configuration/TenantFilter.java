package com.praxis.authentication.configuration;

import java.io.IOException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class TenantFilter extends OncePerRequestFilter {
    
    private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);
    
    private final ClientTenantService clientTenantService;
    
    public TenantFilter(ClientTenantService clientTenantService) {
        this.clientTenantService = clientTenantService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        
        String clientId = extractClientId(request);
        log.info("🔍 [TenantFilter] Procesando {} {} - ClientId: {}", 
            request.getMethod(), request.getRequestURI(), clientId);
        
        // ALWAYS establecer tenant basado en clientId si está disponible
        if (clientId != null) {
            String tenant = clientTenantService.resolveTenant(clientId);
            log.info("🏢 [TenantFilter] Tenant resuelto: {} para clientId: {}", tenant, clientId);
            
            // Guardar en ThreadLocal
            TenantContext.setTenant(tenant);
            TenantContext.setClientId(clientId);
            
            // Guardar en sesión
            HttpSession session = request.getSession(true);
            session.setAttribute("TENANT", tenant);
            session.setAttribute("CLIENT_ID", clientId);
            log.info("📌 [TenantFilter] Tenant {} guardado en sesión: {}", tenant, session.getId());
        } else {
            // Si no hay clientId, usar el tenant de la sesión (para /login, /logout, etc.)
            HttpSession session = request.getSession(false);
            if (session != null) {
                String tenantFromSession = (String) session.getAttribute("TENANT");
                if (tenantFromSession != null) {
                    TenantContext.setTenant(tenantFromSession);
                    log.info("🔄 [TenantFilter] Tenant recuperado de sesión: {}", tenantFromSession);
                    
                    // También restaurar clientId si está disponible
                    String clientIdFromSession = (String) session.getAttribute("CLIENT_ID");
                    if (clientIdFromSession != null) {
                        TenantContext.setClientId(clientIdFromSession);
                    }
                }
            }
        }
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            // Limpiar ThreadLocal al final de la request para evitar leaks
            TenantContext.clear();
        }
    }
    
    private String extractClientId(HttpServletRequest request) {
        // Primero verificar parámetro
        String clientId = request.getParameter("client_id");
        if (clientId != null && !clientId.isEmpty()) {
            return clientId;
        }
        
        // Verificar Basic Auth
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Basic ")) {
            try {
                String base64Credentials = auth.substring("Basic ".length());
                String credentials = new String(Base64.getDecoder().decode(base64Credentials));
                clientId = credentials.split(":")[0];
                return clientId;
            } catch (Exception e) {
                log.warn("Error decodificando Basic Auth: {}", e.getMessage());
            }
        }
        
        return null;
    }
}