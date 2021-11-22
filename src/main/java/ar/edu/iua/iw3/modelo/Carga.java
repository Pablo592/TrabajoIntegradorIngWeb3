package ar.edu.iua.iw3.modelo;

import ar.edu.iua.iw3.modelo.dto.CargaDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.Serializable;
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

}