package com.praxis.authentication.data.entity.mysql;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auth_rol")
public class AuthRol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rolId;

    @Column(name = "rol_descripcion")
    private String nombre;

	public Integer getRolId() {
		return rolId;
	}

	public void setRolId(Integer rolId) {
		this.rolId = rolId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
