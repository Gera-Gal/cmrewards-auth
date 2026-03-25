package com.praxis.authentication.business.security;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.praxis.authentication.data.entity.Usuarios;
import com.praxis.authentication.data.repository.UsuarioRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

	private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
	
	@Autowired
	private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	log.info("--->Consultando usuario:" + username);
		Usuarios usuario = this.usuarioRepository.findByUsuCorreoAndUsuActivo(username, true);
		
		if (usuario == null) {
			throw new UsernameNotFoundException(username );
		}
		
		/*List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();		
		Iterator<Rol> iter = usuario.getRoles().iterator();
		while (iter.hasNext()) {
			Rol rol = iter.next();
	    	SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rol.getDescripcion());
	    	authorities.add(authority);
		}				

        return new org.springframework.security.core.userdetails.User(
            	usuario.getUsuNombre() +" "+ usuario.getUsuApellidoPaterno() +" "+ usuario.getUsuApellidoMaterno(),
            	usuario.getUsuHashPassword(),
                authorities
            );*/
		
		return new UserDetailsImpl(
				usuario.getUsuNombre(),
				usuario.getUsuHashPassword(), null,
				usuario.getUsuUsuario(),
				usuario.getUsuCorreo());		

    }
    
}
