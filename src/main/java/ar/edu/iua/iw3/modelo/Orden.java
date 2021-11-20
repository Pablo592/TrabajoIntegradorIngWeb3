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
	@Column(unique = true, nullable = false)
	private String codigoExterno;
	@Column(nullable = false)
	private Calendar fechaTurno;			//Fecha/Hora en la que el camion tiene turno

	private Calendar fechaPesajeInicial;		//Fecha/Hora en que se llevo acabo el pesaje inicial con el camion vacio

	private Calendar fechaInicioProcesoCarga;	//Fecha/Hora en que se comienza a carga el camion

	private Calendar fechaFinProcesoCarga;		//Fecha/Hora en la cual dejo de cargarse el camion

	private Calendar fechaRecepcionPesajeFinal;	//Fecha/Hora en la cual se peso el camion tras finalizar la carga
	@Column(columnDefinition = "int default 0")
	private int estado = 0;						//estado del proceso en la que se encuentra la orden

	private String password;
	@Column(nullable = true)
	private int frecuencia;						//la frecuencia deberia de variar segun la orden
	@Column(nullable = true)
	private float promedioMasaAcumuladaKg;
	@Column(nullable = true)
	private float promedDensidadProductoKilogramoMetroCub;
	@Column(nullable = true)
	private float promedioTemperaturaProductoCelcius;
	@Column(nullable = true)
	private float promedioCaudalLitroSegundo;
	@Column(nullable = true)
	private float ultimaDensidadProductoKilogramoMetroCub;
	@Column(nullable = true)
	private float ultimaTemperaturaProductoCelcius;
	@Column(nullable = true)
	private float ultimoCaudalLitroSegundo;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_camion")
	private Camion camion;						//Vehiculo a cargar

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;					//Cliente que paga el servicio

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_chofer")
	private Chofer chofer;						//Conductor del camion

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_producto")
	private Producto producto;


	@OneToMany(targetEntity=Carga.class, mappedBy= "orden", fetch = FetchType.LAZY)
	@JsonBackReference
	private List<Carga> cargaList;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCodigoExterno() {
		return codigoExterno;
	}

	public void setCodigoExterno(String numeroDeOrden) {
		this.codigoExterno = numeroDeOrden;
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

	public Calendar getFechaTurno() {
		return fechaTurno;
	}

	public void setFechaTurno(Calendar fechaRecepcion) {
		this.fechaTurno = fechaRecepcion;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ar.edu.iua.iw3.modelo.Camion getCamion() {
		return camion;
	}

	public void setCamion(ar.edu.iua.iw3.modelo.Camion camion) {
		this.camion = camion;
	}

	public int getEstado() {
		return estado;
	}

	public void setEstado(int estado) {
		this.estado = estado;
	}

	public int getFrecuencia() {
		return frecuencia;
	}

	public void setFrecuencia(int frecuencia) {
		this.frecuencia = frecuencia;
	}

	public float getPromedioMasaAcumuladaKg() {
		return promedioMasaAcumuladaKg;
	}

	public void setPromedioMasaAcumuladaKg(float promedioMasaAcumuladaKg) {
		this.promedioMasaAcumuladaKg = promedioMasaAcumuladaKg;
	}

	public float getPromedDensidadProductoKilogramoMetroCub() {
		return promedDensidadProductoKilogramoMetroCub;
	}

	public void setPromedDensidadProductoKilogramoMetroCub(float promedDensidadProductoKilogramoMetroCub) {
		this.promedDensidadProductoKilogramoMetroCub = promedDensidadProductoKilogramoMetroCub;
	}

	public float getPromedioTemperaturaProductoCelcius() {
		return promedioTemperaturaProductoCelcius;
	}

	public void setPromedioTemperaturaProductoCelcius(float promedioTemperaturaProductoCelcius) {
		this.promedioTemperaturaProductoCelcius = promedioTemperaturaProductoCelcius;
	}

	public float getPromedioCaudalLitroSegundo() {
		return promedioCaudalLitroSegundo;
	}

	public void setPromedioCaudalLitroSegundo(float promedioCaudalLitroSegundo) {
		this.promedioCaudalLitroSegundo = promedioCaudalLitroSegundo;
	}

	public float getUltimaDensidadProductoKilogramoMetroCub() {
		return ultimaDensidadProductoKilogramoMetroCub;
	}

	public void setUltimaDensidadProductoKilogramoMetroCub(float ultimaDensidadProductoKilogramoMetroCub) {
		this.ultimaDensidadProductoKilogramoMetroCub = ultimaDensidadProductoKilogramoMetroCub;
	}

	public float getUltimaTemperaturaProductoCelcius() {
		return ultimaTemperaturaProductoCelcius;
	}

	public void setUltimaTemperaturaProductoCelcius(float ultimaTemperaturaProductoCelcius) {
		this.ultimaTemperaturaProductoCelcius = ultimaTemperaturaProductoCelcius;
	}

	public float getUltimoCaudalLitroSegundo() {
		return ultimoCaudalLitroSegundo;
	}

	public void setUltimoCaudalLitroSegundo(float ultimoCaudalLitroSegundo) {
		this.ultimoCaudalLitroSegundo = ultimoCaudalLitroSegundo;
	}

	public List<Carga> getCargaList() {
		return cargaList;
	}

	public void setCargaList(List<Carga> cargaList) {
		this.cargaList = cargaList;
	}

//aca tengo que hacer un metodo que chequee basicamente el contenido de los valores que me llegan en el json
	// tanto para el insert como en el update
}



