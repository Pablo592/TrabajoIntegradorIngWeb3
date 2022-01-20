package ar.edu.iua.iw3.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;


@ApiModel(description = "Esta clase representa al cliente que sera abastecido de combustible.")
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
	@Column(length = 100, nullable = false, unique = true)
	private long contacto;

	@OneToMany(targetEntity = Orden.class, mappedBy = "cliente", fetch = FetchType.LAZY)
	@JsonBackReference
	private List<Orden> ordenList;

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

	public long getContacto() {
		return contacto;
	}

	public void setContacto(long contacto) {
		this.contacto = contacto;
	}

	public List<Orden> getOrdenList() {
		return ordenList;
	}

	public void setOrdenList(List<Orden> ordenList) {
		this.ordenList = ordenList;
	}

	public String checkBasicData(){
		if(getRazonSocial().trim().length()==0)
			return "El atributo 'Razon Social' es obligatorio";
		if(getContacto()<1000000000)
			return "El atributo 'contacto' tiene que ser un numero de telefono valido";
		return null;
	}
}