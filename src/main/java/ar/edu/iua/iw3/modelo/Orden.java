package ar.edu.iua.iw3.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "orden")
public class Orden implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String numeroDeOrden;

	@OneToOne(cascade =  CascadeType.ALL)
	@JoinColumn(name = "id_camion")
	private Camion camion;

	@OneToOne(cascade =  CascadeType.ALL)
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;

	@OneToOne(cascade =  CascadeType.ALL)
	@JoinColumn(name = "id_chofer")
	private Chofer chofer;

	@OneToOne(cascade =  CascadeType.ALL)
	@JoinColumn(name = "id_producto")
	private Producto producto;
	
	private Calendar fechaRecepcion;
	
	private Calendar fechaPesajeInicial;
	
	private Calendar fechaInicioProcesoCarga;
	
	private Calendar fechaFinProcesoCarga;
	
	private Calendar fechaRecepcionPesajeFinal;

	private String password;

	private Double ultimaMasaAcumulada;

	private Double promedioDensidad;

	private Double promedioTemperatura;

	private Double promedioCaudal;

	@OneToMany(targetEntity=Carga.class, mappedBy= "order", fetch = FetchType.LAZY)
	@JsonBackReference
	private List<Carga> cargaList;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNumeroDeOrden() {
		return numeroDeOrden;
	}

	public void setNumeroDeOrden(String numeroDeOrden) {
		this.numeroDeOrden = numeroDeOrden;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
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

	public Calendar getFechaRecepcionPesajeFinal() {
		return fechaRecepcionPesajeFinal;
	}

	public void setFechaRecepcionPesajeFinal(Calendar fechaRecepcionPesajeFinal) {
		this.fechaRecepcionPesajeFinal = fechaRecepcionPesajeFinal;
	}

	public List<Carga> getCargaList() {
		return cargaList;
	}

	public void setCargaList(List<Carga> cargaList) {
		this.cargaList = cargaList;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Double getUltimaMasaAcumulada() {
		return ultimaMasaAcumulada;
	}

	public void setUltimaMasaAcumulada(Double ultimaMasaAcumulada) {
		this.ultimaMasaAcumulada = ultimaMasaAcumulada;
	}

	public Double getPromedioDensidad() {
		return promedioDensidad;
	}

	public void setPromedioDensidad(Double promedioDensidad) {
		this.promedioDensidad = promedioDensidad;
	}

	public Double getPromedioTemperatura() {
		return promedioTemperatura;
	}

	public void setPromedioTemperatura(Double promedioTemperatura) {
		this.promedioTemperatura = promedioTemperatura;
	}

	public Double getPromedioCaudal() {
		return promedioCaudal;
	}

	public void setPromedioCaudal(Double promedioCaudal) {
		this.promedioCaudal = promedioCaudal;
	}

	public ar.edu.iua.iw3.modelo.Camion getCamion() {
		return camion;
	}

	public void setCamion(ar.edu.iua.iw3.modelo.Camion camion) {
		this.camion = camion;
	}
}



