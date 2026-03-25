package com.praxis.authentication.data.entity;

//import java.util.HashSet;
//import java.util.Set;

//import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "Usuarios")
public class Usuarios implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "usu_Usuario", unique = true, nullable = false, precision = 8, scale = 0)
    private Integer usuUsuario;    
    
    @Column(name = "usu_Nombre", nullable = false, length = 100)
    private String usuNombre;
    
    @Column(name = "usu_Apellido_Paterno", nullable = false, length = 30)
    private String usuApellidoPaterno;
    
    @Column(name = "usu_Apellido_Materno", length = 30)
    private String usuApellidoMaterno;    
    
    @Column(name = "usu_Correo", nullable = false, length = 100)
    private String usuCorreo;
    
    @Column(name = "usu_Hash_Password", nullable = false, length = 256)
    private String usuHashPassword;
    
    @Column(name = "usu_Activo")
    private Boolean usuActivo;
    
//    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
//    @JoinColumn(name = "usu_Usuario")    	    	
//    Set<Rol> roles = new HashSet<>();
  
	public Usuarios() {
    }

    public Usuarios(Integer usuUsuario, String usuNombre, String usuApellidoPaterno, String usuApellidoMaterno,
			String usuCorreo, String usuHashPassword) {
		super();
		this.usuUsuario = usuUsuario;
		this.usuNombre = usuNombre;
		this.usuApellidoPaterno = usuApellidoPaterno;
		this.usuApellidoMaterno = usuApellidoMaterno;
		this.usuCorreo = usuCorreo;
		this.usuHashPassword = usuHashPassword;
	}

	public Integer getUsuUsuario() {
		return usuUsuario;
	}

	public void setUsuUsuario(Integer usuUsuario) {
		this.usuUsuario = usuUsuario;
	}

	public String getUsuNombre() {
		return usuNombre;
	}

	public void setUsuNombre(String usuNombre) {
		this.usuNombre = usuNombre;
	}

	public String getUsuApellidoPaterno() {
		return usuApellidoPaterno;
	}

	public void setUsuApellidoPaterno(String usuApellidoPaterno) {
		this.usuApellidoPaterno = usuApellidoPaterno;
	}

	public String getUsuApellidoMaterno() {
		return usuApellidoMaterno;
	}

	public void setUsuApellidoMaterno(String usuApellidoMaterno) {
		this.usuApellidoMaterno = usuApellidoMaterno;
	}

	public String getUsuCorreo() {
		return usuCorreo;
	}

	public void setUsuCorreo(String usuCorreo) {
		this.usuCorreo = usuCorreo;
	}

	public String getUsuHashPassword() {
		return usuHashPassword;
	}

	public void setUsuHashPassword(String usuHashPassword) {
		this.usuHashPassword = usuHashPassword;
	}

//	public Set<Rol> getRoles() {
//		return roles;
//	}
//
//	public void setRoles(Set<Rol> roles) {
//		this.roles = roles;
//	}

	public Boolean getUsuActivo() {
		return usuActivo;
	}

	public void setUsuActivo(Boolean usuActivo) {
		this.usuActivo = usuActivo;
	}

	@Override
	public String toString() {
		return "Usuarios [usuUsuario=" + usuUsuario + ", usuNombre=" + usuNombre + ", usuApellidoPaterno="
				+ usuApellidoPaterno + ", usuApellidoMaterno=" + usuApellidoMaterno + ", usuCorreo=" + usuCorreo
				+ ", usuHashPassword=" + usuHashPassword + ", usuActivo=" + usuActivo + "]";
	}	

}