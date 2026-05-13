package com.praxis.authentication.business.security;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.praxis.authentication.data.entity.sqlserver.Usuarios;
import com.praxis.authentication.data.repository.sqlserver.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	
	@Autowired
	private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	log.info("🔍 [SQL Server] Buscando usuario por username/email: {}", username);
    	
    	long startTime = System.currentTimeMillis();
		Usuarios usuario = this.usuarioRepository.findByUsuCorreoAndUsuActivo(username, true);
		long endTime = System.currentTimeMillis();
		
		log.info("⏱️ [SQL Server] Consulta ejecutada en {} ms", (endTime - startTime));
		
		if (usuario == null) {
			log.error("❌ [SQL Server] Usuario no encontrado: {}", username);
			throw new UsernameNotFoundException("Usuario no encontrado en SQL Server: " + username);
		}
		
		log.info("✅ [SQL Server] Usuario encontrado - ID: {}, Correo: {}, Nombre: {}", 
			usuario.getUsuUsuario(), usuario.getUsuCorreo(), usuario.getUsuNombre());
		
		List<String> authorities = new ArrayList<>();
        authorities.add("ROLE_PARTICIPANT");
        
        log.info("📋 [SQL Server] Authorities asignadas: {}", authorities);
        
        UserDetailsImpl userDetails = new UserDetailsImpl(
				usuario.getUsuNombre(),
				usuario.getUsuHashPassword(),
				authorities,
				usuario.getUsuUsuario(),
				usuario.getUsuCorreo(),
				1);
        
        log.info("🎉 [SQL Server] Usuario autenticado exitosamente: {}, Authorities: {}", 
        	userDetails.getUsername(), authorities);
		
		return userDetails;	
    }
    
}