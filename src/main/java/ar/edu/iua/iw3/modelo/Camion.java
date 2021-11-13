package ar.edu.iua.iw3.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "camion")
public class Camion  implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(length = 7, nullable = false, unique = true)
	private String patente;
	
	@Column(length = 100, nullable = true)
	private String descripcion;
	
	@Column(columnDefinition = "DOUBLE", nullable = false)
	private double cisternadoLitros;

	@Column(columnDefinition = "DOUBLE", nullable = false)
	private double preset;

	@Column(columnDefinition = "DOUBLE", nullable = false)
	private double tara;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPatente() {
		return patente;
	}

	public void setPatente(String patente) {
		this.patente = patente;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getCisternadoLitros() {
		return cisternadoLitros;
	}

	public void setCisternadoLitros(double cisternadoLitros) {
		this.cisternadoLitros = cisternadoLitros;
	}

	public double getPreset() {
		return preset;
	}

	public void setPreset(double preset) {
		this.preset = preset;
	}

	public double getTara() {
		return tara;
	}

	public void setTara(double tara) {
		this.tara = tara;
	}
}
