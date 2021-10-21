package ar.edu.iua.iw3.modelo;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "orden")
public class Orden implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	
	private Long numeroDeOrden;
	
	
	private Camion camion;
	private Cliente ciente;
	private Chofer chofer;
	private Producto producto;
	
	private Calendar fechaRecepcion;
	
	
	
	private Calendar fechaPesajeInicial;
	
	private Calendar fechaInicioProcesoCarga;
	
	private Calendar fechaFinProcesoCarga;
	
	private Calendar fechaPesajeFinal;
	
	
	private Carga carga;

	
	
	
	public long getId() {
		return id;
	}




	public void setId(long id) {
		this.id = id;
	}




	public Long getNumeroDeOrden() {
		return numeroDeOrden;
	}




	public void setNumeroDeOrden(Long numeroDeOrden) {
		this.numeroDeOrden = numeroDeOrden;
	}




	public Camion getCamion() {
		return camion;
	}




	public void setCamion(Camion camion) {
		this.camion = camion;
	}




	public Cliente getCiente() {
		return ciente;
	}




	public void setCiente(Cliente ciente) {
		this.ciente = ciente;
	}




	public Chofer getChofer() {
		return chofer;
	}




	public void setChofer(Chofer chofer) {
		this.chofer = chofer;
	}




	public Producto getProducto() {
		return producto;
	}




	public void setProducto(Producto producto) {
		this.producto = producto;
	}




	public Calendar getFechaRecepcion() {
		return fechaRecepcion;
	}




	public void setFechaRecepcion(Calendar fechaRecepcion) {
		this.fechaRecepcion = fechaRecepcion;
	}




	public Calendar getFechaPesajeInicial() {
		return fechaPesajeInicial;
	}




	public void setFechaPesajeInicial(Calendar fechaPesajeInicial) {
		this.fechaPesajeInicial = fechaPesajeInicial;
	}




	public Calendar getFechaInicioProcesoCarga() {
		return fechaInicioProcesoCarga;
	}




	public void setFechaInicioProcesoCarga(Calendar fechaInicioProcesoCarga) {
		this.fechaInicioProcesoCarga = fechaInicioProcesoCarga;
	}




	public Calendar getFechaFinProcesoCarga() {
		return fechaFinProcesoCarga;
	}




	public void setFechaFinProcesoCarga(Calendar fechaFinProcesoCarga) {
		this.fechaFinProcesoCarga = fechaFinProcesoCarga;
	}




	public Calendar getFechaPesajeFinal() {
		return fechaPesajeFinal;
	}




	public void setFechaPesajeFinal(Calendar fechaPesajeFinal) {
		this.fechaPesajeFinal = fechaPesajeFinal;
	}




	public Carga getCarga() {
		return carga;
	}




	public void setCarga(Carga carga) {
		this.carga = carga;
	}




	private class Carga implements Serializable {

		private static final long serialVersionUID = 1L;
		
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private long id;
		
		private float masaAcumuladaKg;
		
		private float densidadProductoKilogramoLitro;
		
		private float temperaturaProductoCelcius;
		
		private float caudalLitroSegundo;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public float getMasaAcumuladaKg() {
			return masaAcumuladaKg;
		}

		public void setMasaAcumuladaKg(float masaAcumuladaKg) {
			this.masaAcumuladaKg = masaAcumuladaKg;
		}

		public float getDensidadProductoKilogramoLitro() {
			return densidadProductoKilogramoLitro;
		}

		public void setDensidadProductoKilogramoLitro(float densidadProductoKilogramoLitro) {
			this.densidadProductoKilogramoLitro = densidadProductoKilogramoLitro;
		}

		public float getTemperaturaProductoCelcius() {
			return temperaturaProductoCelcius;
		}

		public void setTemperaturaProductoCelcius(float temperaturaProductoCelcius) {
			this.temperaturaProductoCelcius = temperaturaProductoCelcius;
		}

		public float getCaudalLitroSegundo() {
			return caudalLitroSegundo;
		}

		public void setCaudalLitroSegundo(float caudalLitroSegundo) {
			this.caudalLitroSegundo = caudalLitroSegundo;
		}
		
		
		
	}
}



