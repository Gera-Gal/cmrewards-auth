package com.praxis.authentication.data.repository.sqlserver;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.praxis.authentication.data.entity.sqlserver.Usuarios;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuarios, Integer>{
	
	public Usuarios findByUsuCorreoAndUsuActivo(String usuCorreo,Boolean activo);
	
}
