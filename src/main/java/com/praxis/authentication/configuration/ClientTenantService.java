package com.praxis.authentication.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ClientTenantService {
	
	private static final Logger log = LoggerFactory.getLogger(ClientTenantService.class);

    public String resolveTenant(String clientId) {
    	
    	log.info("🏢 Resolviendo tenant para clientId: {}", clientId);

        if ("lealtad-clientCmpay".equals(clientId)) {
            log.info("✅ Tenant resuelto: SQL_SERVER para clientId: {}", clientId);
            return "SQL_SERVER";
        }
        
        if ("lealtad-client".equals(clientId)) {
            log.info("✅ Tenant resuelto: SQL_SERVER para clientId: {}", clientId);
            return "SQL_SERVER";
        }

        if ("lealtad-clientAdmin".equals(clientId)) {
            log.info("✅ Tenant resuelto: MYSQL para clientId: {}", clientId);
            return "MYSQL";
        }
        
        log.error("❌ Client no reconocido: {}", clientId);
        throw new RuntimeException("Client no reconocido: " + clientId);
    }
}