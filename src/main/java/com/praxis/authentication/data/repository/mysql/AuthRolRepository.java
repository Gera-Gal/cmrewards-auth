package com.praxis.authentication.data.repository.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.praxis.authentication.data.entity.mysql.AuthRol;

@Repository
public interface AuthRolRepository extends JpaRepository<AuthRol, Integer> {

    AuthRol findByRolId(Integer rolId);
}
