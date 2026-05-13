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
        
        // Primero intentar obtener tenant de ThreadLocal
        String tenant = TenantContext.getTenant();
        
        // Si no está en ThreadLocal, intentar obtener de la sesión
        if (tenant == null) {
            try {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpSession session = attributes.getRequest().getSession(false);
                    if (session != null) {
                        tenant = (String) session.getAttribute("TENANT");
                        log.info("🔄 [MultiTenantUserDetailsService] Tenant recuperado de sesión: {}", tenant);
                    }
                }
            } catch (Exception e) {
                log.warn("No se pudo acceder a la sesión: {}", e.getMessage());
            }
        }
        
        log.info("🏢 [MultiTenantUserDetailsService] Tenant actual: {}", tenant);
        log.info("🔍 Buscando usuario: {}", username);
        
        if (tenant == null) {
            log.error("❌ Tenant es null - no se puede determinar qué base de datos usar");
            throw new UsernameNotFoundException("No se pudo determinar el tenant para el usuario: " + username);
        }
        
        if ("SQL_SERVER".equals(tenant)) {
            log.info("📌 Usando UserDetailsServiceImpl (SQL Server)");
            return sqlServerService.loadUserByUsername(username);
        } else if ("MYSQL".equals(tenant)) {
            log.info("📌 Usando UserDetailsServiceImplMySQL (MySQL)");
            return mySQLService.loadUserByUsername(username);
        } else {
            log.error("❌ Tenant no reconocido: {}", tenant);
            throw new UsernameNotFoundException("Tenant no reconocido: " + tenant);
        }
    }
}