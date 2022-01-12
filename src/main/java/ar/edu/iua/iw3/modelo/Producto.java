package ar.edu.iua.iw3.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

@ApiModel(description = "Esta clase representa al combustible introducido en el camión.")
@Entity
@Table(name = "producto")
public class Producto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ApiModelProperty(notes = "Nombre del producto.", example = "Gas", required = true)
	@Column(length = 50, nullable = false, unique = true)
	private String nombre;

	@ApiModelProperty(notes = "Descripción del producto.", example = "Alto octanaje")
	@Column(length = 100)
	private String descripcion;

	@ApiModelProperty(notes = "Un producto puede estar presente en varias ordenes.")
	@OneToMany(targetEntity = Orden.class, mappedBy = "producto", fetch = FetchType.LAZY)
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

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public List<Orden> getOrdenList() {
		return ordenList;
	}

	public void setOrdenList(List<Orden> ordenList) {
		this.ordenList = ordenList;
	}

	public String checkBasicData(){
		if(getNombre().trim().length()==0)
			return "El atributo 'Producto' es obligatorio";
		return null;
	}
}
