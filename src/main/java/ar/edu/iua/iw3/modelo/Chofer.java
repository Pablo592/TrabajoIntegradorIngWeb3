package ar.edu.iua.iw3.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

@ApiModel(description = "Esta clase representa al chófer del camión que recibirá combustible.")
@Entity
@Table(name = "chofer")
public class Chofer implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ApiModelProperty(notes = "Nombre del chofer.", example = "Carlos", required = true)
	@Column(length = 100, nullable = false)
	private String nombre;

	@ApiModelProperty(notes = "Apellido del chofer.", example = "Gomez", required = true)
	@Column(length = 100, nullable = false)
	private String apellido;

	@ApiModelProperty(notes = "DNI del chofer.", example = "40526398", required = true)
	@Column(length = 8, nullable = false, unique = true)
	private long documento;

	@ApiModelProperty(notes = "Un chófer puede requerir una orden en varias ocasiones.")
	@OneToMany(targetEntity = Orden.class, mappedBy = "chofer", fetch = FetchType.LAZY)
	@JsonBackReference
	private List<Orden> ordenList;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public long getDocumento() {
		return documento;
	}

	public void setDocumento(long documento) {
		this.documento = documento;
	}

	public List<Orden> getOrdenList() {
		return ordenList;
	}

	public void setOrdenList(List<Orden> ordenList) {
		this.ordenList = ordenList;
	}

	public String checkBasicData(){
		if(getNombre().trim().length()==0)
			return "El atributo 'nombre' es obligatorio";
		if(getApellido().trim().length()==0)
			return "El atributo 'apellido' es obligatorio";
		if(getDocumento() < 4000000 || getDocumento()>100000000)
			return "El atributo 'documento' tiene que ser entre 4 a 100 millones";
		return null;
	}
}