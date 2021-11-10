package ar.edu.iua.iw3.modelo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@ApiModel(description = "Class representing a client in the application.")
@Entity
@Table(name = "cliente")
public class Cliente implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ApiModelProperty(notes = "Razon Social",
			example = "SA", required = true)
	@Column(length = 100, nullable = false)
	private String razonSocial;

	@ApiModelProperty(notes = "Numero de telefono del cliente",
			example = "20", required = false, allowableValues = "range[3500000000, 35499999999]")
	@Column(length = 100, nullable = true)
	private Long contacto;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	@ApiModelProperty(required = true)
	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public Long getContacto() {
		return contacto;
	}

	public void setContacto(Long contacto) {
		this.contacto = contacto;
	}
	
	
	
}