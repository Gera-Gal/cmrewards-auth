package com.praxis.authentication.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * Entidad Usuario
 *
 */
@Entity
@Table(name = "Restaurantes_Roles")
public class Rol implements java.io.Serializable { 

    private static final long serialVersionUID = 1L;
    
    // PK
    @Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rol_seq")
    @SequenceGenerator(sequenceName = "sq_restaurantes_rol", allocationSize = 1, name = "rol_seq")	
    @Column(name = "rol_Id", unique = true, nullable = false)
    private int id;
    
    @Column(name = "usu_Usuario", precision = 8, scale = 0)
    Integer usuUsuario;
    
    @Column(name = "descripcion", length = 30)
    private String descripcion;
    
    
    public Rol() {}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Integer getUsuUsuario() {
		return usuUsuario;
	}


	public void setUsuUsuario(Integer usuUsuario) {
		this.usuUsuario = usuUsuario;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	@Override
	public String toString() {
		return "Rol [id=" + id + ", usuUsuario=" + usuUsuario + ", descripcion=" + descripcion + "]";
	}

}
