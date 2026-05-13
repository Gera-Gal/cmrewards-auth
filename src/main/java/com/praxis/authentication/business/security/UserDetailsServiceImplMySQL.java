package com.praxis.authentication.business.security;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.praxis.authentication.data.entity.mysql.AuthRol;
import com.praxis.authentication.data.entity.mysql.AuthUsuario;
import com.praxis.authentication.data.repository.mysql.AuthRolRepository;
import com.praxis.authentication.data.repository.mysql.AuthUsuarioRepository;

@Service
public class UserDetailsServiceImplMySQL implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImplMySQL.class);

    @Autowired
    private AuthUsuarioRepository usuarioRepository;
    
    @Autowired
    private AuthRolRepository rolRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        log.info("🔍 [MySQL] Buscando usuario por username/email: {}", username);
        
        long startTime = System.currentTimeMillis();
        AuthUsuario usuario = usuarioRepository.findByCorreoAndActivo(username, "1");
        long endTime = System.currentTimeMillis();
        
        log.info("⏱️ [MySQL] Consulta ejecutada en {} ms", (endTime - startTime));

        if (usuario == null) {
            log.error("❌ [MySQL] Usuario no encontrado: {}", username);
            throw new UsernameNotFoundException("Usuario no encontrado en MySQL: " + username);
        }
        
        log.info("✅ [MySQL] Usuario encontrado - ID: {}, Correo: {}, EntId: {}, RolId: {}", 
            usuario.getUsuId(), usuario.getCorreo(), usuario.getEntId(), usuario.getRolId());
        
        long rolStartTime = System.currentTimeMillis();
        AuthRol rol = rolRepository.findByRolId(usuario.getRolId());
        long rolEndTime = System.currentTimeMillis();
        
        log.info("⏱️ [MySQL] Consulta de rol ejecutada en {} ms", (rolEndTime - rolStartTime));
        
        List<String> authorities = new ArrayList<>();

        if (rol != null) {
            authorities.add("ROLE_" + rol.getNombre());
            log.info("📋 [MySQL] Rol encontrado: ID={}, Nombre={}, Authority asignada: ROLE_{}", 
                rol.getRolId(), rol.getNombre(), rol.getNombre());
        } else {
            authorities.add("ROLE_PARTICIPANT");
            log.warn("⚠️ [MySQL] No se encontró rol para rolId: {}, usando ROLE_PARTICIPANT por defecto", 
                usuario.getRolId());
        }
        
        log.info("📋 [MySQL] Authorities asignadas: {}", authorities);

        UserDetailsImpl user = new UserDetailsImpl(
                usuario.getCorreo(),
                usuario.getPassword(),
                authorities,
                usuario.getUsuId(),
                usuario.getCorreo(),
                usuario.getEntId()
        );
        
        log.info("🎉 [MySQL] Usuario autenticado exitosamente: {}, Authorities: {}", 
            user.getUsername(), authorities);

        return user;
    }
}