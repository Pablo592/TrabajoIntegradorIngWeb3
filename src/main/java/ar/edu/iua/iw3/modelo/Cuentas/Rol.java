package ar.edu.iua.iw3.modelo.Cuentas;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="roles")
public class Rol implements Serializable{

	private static final long serialVersionUID = 1139806825119468503L;
 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(length = 50, unique = true, nullable = false )
	private String nombre;
	@Column(length = 150, nullable = true)	
	private String descripcion;
	public int getId() {
		return id;
	}
	public void setId(int id) {
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
}
