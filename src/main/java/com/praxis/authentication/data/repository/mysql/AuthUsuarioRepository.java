package com.praxis.authentication.data.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.praxis.authentication.data.entity.mysql.AuthUsuario;

@Repository
public interface AuthUsuarioRepository extends JpaRepository<AuthUsuario, Integer> {

    AuthUsuario findByCorreoAndActivo(String correo, String activo);
    
}