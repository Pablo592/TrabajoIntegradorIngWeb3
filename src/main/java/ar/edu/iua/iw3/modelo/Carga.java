package ar.edu.iua.iw3.modelo;

import ar.edu.iua.iw3.modelo.dto.CargaDTO;

import javax.persistence.*;
import java.io.Serializable;
@NamedNativeQueries({

        @NamedNativeQuery(name = "Carga.getMasaAcuAndPromedioDensidadAndTemperaturaAndCaudal",
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


@Entity
@Table(name = "carga")
public class Carga implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private float masaAcumuladaKg;

    private float densidadProductoKilogramoMetroCub;

    private float temperaturaProductoCelcius;

    private float caudalLitroSegundo;


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