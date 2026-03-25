package com.praxis.authentication.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.praxis.authentication.data.entity.Rol;

@Repository
public interface PerfilRepository extends JpaRepository<Rol, Integer>{

}
