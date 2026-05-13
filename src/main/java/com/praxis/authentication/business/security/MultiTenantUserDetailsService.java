package com.praxis.authentication.business.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.praxis.authentication.configuration.TenantContext;

import jakarta.servlet.http.HttpSession;

@Service
public class MultiTenantUserDetailsService implements UserDetailsService {
    
    private static final Logger log = LoggerFactory.getLogger(MultiTenantUserDetailsService.class);
    
    @Autowired
    private UserDetailsServiceImpl sqlServerService;
    
    @Autowired
    private UserDetailsServiceImplMySQL mySQLService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // PRIORIDAD 1: ThreadLocal (más confiable)
        String tenant = TenantContext.getTenant();
        
        // PRIORIDAD 2: Sesión HTTP (fallback)
        if (tenant == null) {
            try {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpSession session = attributes.getRequest().getSession(false);
                    if (session != null) {
                        tenant = (String) session.getAttribute("TENANT");
                        log.info("🔄 Tenant recuperado de sesión: {}", tenant);
                    }
                }
            } catch (Exception e) {
                log.warn("No se pudo acceder a la sesión: {}", e.getMessage());
            }
        }
        
        log.info("🏢 Tenant actual: {} para usuario: {}", tenant, username);
        
        if (tenant == null) {
            log.error("❌ Tenant es null - no se puede determinar qué base de datos usar");
            throw new UsernameNotFoundException("No se pudo determinar el tenant para el usuario: " + username);
        }
        
        UserDetailsService service = getServiceForTenant(tenant);
        if (service == null) {
            log.error("❌ No hay servicio para tenant: {}", tenant);
            throw new UsernameNotFoundException("Tenant no soportado: " + tenant);
        }
        
        log.info("✅ Usando servicio para tenant: {}", tenant);
        return service.loadUserByUsername(username);
    }
    
    private UserDetailsService getServiceForTenant(String tenant) {
        if ("SQL_SERVER".equals(tenant)) {
            return sqlServerService;
        } else if ("MYSQL".equals(tenant)) {
            return mySQLService;
        }
        return null;
    }
}