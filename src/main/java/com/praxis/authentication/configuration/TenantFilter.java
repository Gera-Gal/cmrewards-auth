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

        try {
            String clientId = extractClientId(request);
            
            log.info("🔍 [TenantFilter] Procesando {} {}", request.getMethod(), request.getRequestURI());
            log.info("🔍 [TenantFilter] Client ID extraído: {}", clientId);

            if (clientId != null) {
                String tenant = clientTenantService.resolveTenant(clientId);
                log.info("🏢 [TenantFilter] Tenant resuelto: {} para clientId: {}", tenant, clientId);

                // Guardar en ThreadLocal para el request actual
                TenantContext.setTenant(tenant);
                TenantContext.setClientId(clientId);
                
                // ✅ GUARDAR EN SESIÓN para peticiones posteriores
                HttpSession session = request.getSession(true);
                session.setAttribute("TENANT", tenant);
                session.setAttribute("CLIENT_ID", clientId);
                log.info("📌 [TenantFilter] Tenant guardado en sesión: {} (Session ID: {})", tenant, session.getId());
            } else {
                // ✅ Si no hay client_id, intentar recuperar de la sesión
                HttpSession session = request.getSession(false);
                if (session != null) {
                    String tenantFromSession = (String) session.getAttribute("TENANT");
                    if (tenantFromSession != null) {
                        TenantContext.setTenant(tenantFromSession);
                        log.info("🔄 [TenantFilter] Tenant recuperado de sesión: {}", tenantFromSession);
                    } else {
                        log.warn("⚠️ [TenantFilter] No se pudo extraer clientId ni hay tenant en sesión");
                    }
                } else {
                    log.warn("⚠️ [TenantFilter] No se pudo extraer clientId y no hay sesión");
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            // No limpiar TenantContext aquí, si queremos que persista para el resto de la request
            // TenantContext.clear();
        }
    }

    private String extractClientId(HttpServletRequest request) {

        String clientId = request.getParameter("client_id");
        if (clientId != null) {
            log.debug("   Client ID extraído de query parameter: {}", clientId);
            return clientId;
        }

        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Basic ")) {
            String base64Credentials = auth.substring("Basic ".length());
            String credentials = new String(Base64.getDecoder().decode(base64Credentials));
            clientId = credentials.split(":")[0];
            log.debug("   Client ID extraído de Basic Auth: {}", clientId);
            return clientId;
        }

        return null;
    }
}