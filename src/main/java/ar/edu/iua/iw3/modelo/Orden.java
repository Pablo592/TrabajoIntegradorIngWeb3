package ar.edu.iua.iw3.modelo;

import ar.edu.iua.iw3.modelo.dto.ConciliacionDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
@NamedNativeQueries({

		@NamedNativeQuery(name = "Orden.getPesoInicialAndPesoFinalAndMasaAcumuladaKgAndDiferenciaMasaAcu_DeltaPeso",
				query = "SELECT c.tara as pesajeInicial , \n" +
						"\t c.peso_final_camion as pesajeFinal, \n" +
						"\t masa_acumulada_kg, \n" +
						"\t (c.peso_final_camion - c.tara) as netoPorBalanza, \n" +
						"\t  ((c.peso_final_camion - c.tara) - masa_acumulada_kg ) as diferenciaNetoPorBalanza_masaAcumuludada, \n" +
						"\t  o.promedio_temperatura_producto_celcius, \n" +
						"\t  o.promedio_caudal_litro_segundo,\n" +
						"\t  o.promed_densidad_producto_kilogramo_metro_cub\n" +
						"\t from orden o \n" +
						"\t inner join camion c on c.id = o.id_camion \n" +
						"\t where o.id = ?1", resultSetMapping = "ordenmap")

})

@SqlResultSetMapping(
		name="ordenmap",
		classes = {
				@ConstructorResult(
						columns = {
								@ColumnResult(name = "pesajeInicial", type = float.class),
								@ColumnResult(name = "pesajeFinal", type = float.class),
								@ColumnResult(name = "masa_acumulada_kg", type = float.class),
								@ColumnResult(name = "netoPorBalanza", type = float.class),
								@ColumnResult(name = "diferenciaNetoPorBalanza_masaAcumuludada", type = float.class),
								@ColumnResult(name = "promedio_temperatura_producto_celcius", type = float.class),
								@ColumnResult(name = "promedio_caudal_litro_segundo", type = float.class),
								@ColumnResult(name = "promed_densidad_producto_kilogramo_metro_cub", type = float.class)
						},
						targetClass = ConciliacionDTO.class
				)
		}
)



@ApiModel(description = "Esta clase representa a la orden encargada de registrar la carga de combustible.")
@Entity
@Table(name = "orden")
public class Orden implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@ApiModelProperty(notes = "Clave principal del sistema externo.", example = "5", required = true)
	@Column(unique = true, nullable = false)
	private String codigoExterno;

	@ApiModelProperty(notes = "Fecha prevista de la carga de combustible.", example = "2021-01-01", required = true)
	@Column(nullable = false)
	private Date fechaTurno;

	@ApiModelProperty(notes = "Fecha del pesaje del camión sin carga.", example = "2021-01-01")
	private Date fechaPesajeInicial;

	@ApiModelProperty(notes = "Fecha del inicio del proceso de carga.", example = "2021-01-01")
	private Date fechaInicioProcesoCarga;

	@ApiModelProperty(notes = "Fecha del fin del proceso de carga,con precision de microSegundos.", example = "2021-01-01")
	private Date fechaFinProcesoCarga;

	@ApiModelProperty(notes = "Fecha del pesaje final del camión.", example = "2021-01-01")
	private Date fechaRecepcionPesajeFinal;

	@ApiModelProperty(notes = "Estado actual de la orden.", example = "2")
	@Column(columnDefinition = "int default 0")
	private int estado = 0;

	@ApiModelProperty(notes = "Contraseña generada al registrar el pesaje inicial del camión.", example = "65485")
	private String password;

	@ApiModelProperty(notes = "Frecuencia en segundos del registro de carga del camión", example = "1")
	@Column(columnDefinition = "int default 1")
	private int frecuencia = 1;

	@ApiModelProperty(notes = "Peso del combustible (kG) ya cargado.", example = "30")
	private float masaAcumuladaKg;

	@ApiModelProperty(notes = "Densidad promedio del combustible (kg/m^3).", example = "0,874")
	private float promedDensidadProductoKilogramoMetroCub;

	@ApiModelProperty(notes = "Temperatura promedio del combustible (°C).", example = "16")
	private float promedioTemperaturaProductoCelcius;

	@ApiModelProperty(notes = "Cantidad promedio de combustible cargado por segundo (litro/segundo).", example = "0,16")
	private float promedioCaudalLitroSegundo;

	@ApiModelProperty(notes = "Ultima densidad registrada del combustible (kg/m^3).", example = "0,874")
	private float ultimaDensidadProductoKilogramoMetroCub;

	@ApiModelProperty(notes = "Ultima temperatura registrada del combustible (°C).", example = "16")
	private float ultimaTemperaturaProductoCelcius;

	@ApiModelProperty(notes = "Ultimo caudal registrado (litro/segundo).", example = "0,16")
	private float ultimoCaudalLitroSegundo;

	@ApiModelProperty(notes = "El camión que sera cargado con combustible")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_camion")
	private Camion camion;

	@ApiModelProperty(notes = "El cliente al que se le entregara el combustible")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;

	@ApiModelProperty(notes = "El chofer al que se le entregara el combustible")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_chofer")
	private Chofer chofer;

	@ApiModelProperty(notes = "El combustible introducido en el camión")
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_producto")
	private Producto producto;

	@ApiModelProperty(notes = "Las cargas de combustible introducidas en el camión")
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

	public Date getFechaTurno() {
		return fechaTurno;
	}

	public void setFechaTurno(Date fechaRecepcion) {
		this.fechaTurno = fechaRecepcion;
	}

	public Date getFechaPesajeInicial() {
		return fechaPesajeInicial;
	}

	public void setFechaPesajeInicial(Date fechaPesajeInicial) {
		this.fechaPesajeInicial = fechaPesajeInicial;
	}

	public Date getFechaInicioProcesoCarga() {
		return fechaInicioProcesoCarga;
	}

	public void setFechaInicioProcesoCarga(Date fechaInicioProcesoCarga) {
		this.fechaInicioProcesoCarga = fechaInicioProcesoCarga;
	}

	public Date getFechaFinProcesoCarga() {
		return fechaFinProcesoCarga;
	}

	public void setFechaFinProcesoCarga(Date fechaFinProcesoCarga) {
		this.fechaFinProcesoCarga = fechaFinProcesoCarga;
	}

	public Date getFechaRecepcionPesajeFinal() {
		return fechaRecepcionPesajeFinal;
	}

	public void setFechaRecepcionPesajeFinal(Date fechaRecepcionPesajeFinal) {
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

	public float getMasaAcumuladaKg() {
		return masaAcumuladaKg;
	}

	public void setMasaAcumuladaKg(float promedioMasaAcumuladaKg) {
		this.masaAcumuladaKg = promedioMasaAcumuladaKg;
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


	@Override
	public int hashCode() {
		return Objects.hash(id, codigoExterno, fechaTurno, fechaPesajeInicial, fechaInicioProcesoCarga, fechaFinProcesoCarga, fechaRecepcionPesajeFinal, estado, password, frecuencia, masaAcumuladaKg, promedDensidadProductoKilogramoMetroCub, promedioTemperaturaProductoCelcius, promedioCaudalLitroSegundo, ultimaDensidadProductoKilogramoMetroCub, ultimaTemperaturaProductoCelcius, ultimoCaudalLitroSegundo, camion, cliente, chofer, producto, cargaList);
	}
}



