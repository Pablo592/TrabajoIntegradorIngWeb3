package ar.edu.iua.iw3.modelo;

import ar.edu.iua.iw3.modelo.dto.CargaDTO;
import ar.edu.iua.iw3.negocio.excepciones.ConflictException;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NamedNativeQueries({

        @NamedNativeQuery(name = "Carga.getPromedioDensidadAndTemperaturaAndCaudal",
                query = "SELECT  avg(c.caudal_litro_segundo) as promedioCaudalLitroSegundo, \n" +
                        "\t avg(c.densidad_producto_kilogramo_metro_cub) as promedioDensidadProductoKilogramoMetroCub , \n" +
                        "\t avg(c.temperatura_producto_celcius) as promedioTemperaturaProductoCelcius \n" +
                        "\t from carga c \n" +
                "\t where c.orden_id = ?1", resultSetMapping = "cargamap")

})

@SqlResultSetMapping(
        name="cargamap",
        classes = {
                @ConstructorResult(
                        columns = {
                                @ColumnResult(name = "promedioCaudalLitroSegundo", type = float.class),
                                @ColumnResult(name = "promedioDensidadProductoKilogramoMetroCub", type = float.class),
                                @ColumnResult(name = "promedioTemperaturaProductoCelcius", type = float.class)
                        },
                        targetClass = CargaDTO.class
                )
        }
)

@ApiModel(description = "Esta clase representa la carga continua de combustible en el camión.")
@Entity
@Table(name = "carga")
public class Carga implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ApiModelProperty(notes = "Peso del combustible (kg) ya cargado.", example = "30", required = true)
    private float masaAcumuladaKg;

    @ApiModelProperty(notes = "Densidad del combustible (kg/m^3).", example = "0,874", required = true)
    private float densidadProductoKilogramoMetroCub;

    @ApiModelProperty(notes = "Temperatura del combustible (°C).", example = "16", required = true)
    private float temperaturaProductoCelcius;

    @ApiModelProperty(notes = "Cantidad de combustible cargado por segundo (litro/segundo).", example = "0,16", required = true)
    private float caudalLitroSegundo;

    @ApiModelProperty(notes = "Momento del tiempo en que el HW envio la carga", example = "2021-01-01 16:20:10.95100")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSSSSS")
    private Date fechaSalidaHW;

    @ApiModelProperty(notes = "Momento del tiempo en que el HW envio la carga", example = "2021-01-01 16:21:10.95100")
    @Column(length = 0)
    private Date fechaEntradaBackEnd;

    @ApiModelProperty(notes = "Pueden haber muchas cargas en una orden.")
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "orden_id")
    private Orden orden;

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

    public float getDensidadProductoKilogramoMetroCub() {
        return densidadProductoKilogramoMetroCub;
    }

    public void setDensidadProductoKilogramoMetroCub(float densidadProductoKilogramoLitro) {
        this.densidadProductoKilogramoMetroCub = densidadProductoKilogramoLitro;
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

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    public Date getFechaSalidaHW() {
        return fechaSalidaHW;
    }

    public void setFechaSalidaHW(Date fechaSalidaHW) {
        this.fechaSalidaHW = fechaSalidaHW;
    }

    public Date getFechaEntradaBackEnd() {
        return fechaEntradaBackEnd;
    }

    public void setFechaEntradaBackEnd(Date fechaEntradaBackEnd) {
        this.fechaEntradaBackEnd = fechaEntradaBackEnd;
    }

    public String checkBasicData(){
        if(getMasaAcumuladaKg()<=0)
            return "El atributo 'MasaAcumuladaKg' tiene que ser mayor a cero";
        if(getDensidadProductoKilogramoMetroCub()<=0)
            return "El atributo 'DensidadProductoKilogramoMetroCub' no puede ser menor cero";
        if(getTemperaturaProductoCelcius()>100 || getTemperaturaProductoCelcius()<-60)
            return "El atributo 'TemperaturaProductoCelcius' tiene que ser entre -60 a 100 ºC ";
        if(getCaudalLitroSegundo()<=0)
            return "El atributo 'caudalLitroSegundo' no puede ser menor a cero";
        return null;
    }

    public void checkFechasSalidaEsMenorFechaLlegada() throws ConflictException {
        if(getFechaSalidaHW().compareTo(getFechaEntradaBackEnd())>0)
            throw new ConflictException("El tiempo en que llego la carga requerida al servidor fue antes del tiempo" +
                    "en salio del hadward");
    }

}