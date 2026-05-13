package com.praxis.authentication.data.entity.mysql;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auth_usuario")
public class AuthUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer usuId;

    @Column(name = "usu_correo")
    private String correo;

    @Column(name = "usu_contrasena")
    private String password;

    @Column(name = "usu_activo")
    private String activo;

    @Column(name = "ent_id")
    private Integer entId;

    @Column(name = "rol_id")
    private Integer rolId;

	public Integer getUsuId() {
		return usuId;
	}

	public void setUsuId(Integer usuId) {
		this.usuId = usuId;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getActivo() {
		return activo;
	}

	public void setActivo(String activo) {
		this.activo = activo;
	}

	public Integer getEntId() {
		return entId;
	}

	public void setEntId(Integer entId) {
		this.entId = entId;
	}

	public Integer getRolId() {
		return rolId;
	}

	public void setRolId(Integer rolId) {
		this.rolId = rolId;
	}

}