package ar.edu.iua.iw3.modelo;


import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;
import java.util.Date;

@ApiModel(description = "Esta clase indica los campos que se configuraran en la alarma.")
@Entity
@Table
public class Alarma {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String descripcion;


    @ApiModelProperty(notes = "Una alarma tiene un unico autor.")
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    @ApiModelProperty(notes = "Una alarma tiene un unico autor.")
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "aceptador_id")
    private Usuario usuarioAceptador;

    @ApiModelProperty(notes = "Muchas alarma se asocia a una unica orden")
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "orden_id")
    private Orden ordenAlarma;
    @ApiModelProperty(notes = "Fecha y hora en que el usuario acepta la alarma")
    private Date  fechaAceptacion;

    @ApiModelProperty(notes = "Fecha y Hora en que se genera la alarma")
    private Date fechaDeCreacion;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getFechaDeCreacion() {
        return fechaDeCreacion;
    }

    public void setFechaDeCreacion(Date fechaDeCreacion) {
        this.fechaDeCreacion = fechaDeCreacion;
    }

    public Usuario getUsuarioAceptador() {
        return usuarioAceptador;
    }

    public void setUsuarioAceptador(Usuario usuarioAceptador) {
        this.usuarioAceptador = usuarioAceptador;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public Date getFechaAceptacion() {
        return fechaAceptacion;
    }

    public void setFechaAceptacion(Date fechaAceptacion) {
        this.fechaAceptacion = fechaAceptacion;
    }

    public Orden getOrdenAlarma() {
        return ordenAlarma;
    }

    public void setOrdenAlarma(Orden ordenAlarma) {
        this.ordenAlarma = ordenAlarma;
    }

    @Override
    public String toString() {
        return "Alarma{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", autor=" + autor +
                ", usuarioAceptador=" + usuarioAceptador +
                ", orden=" + ordenAlarma +
                ", fechaAceptacion=" + fechaAceptacion +
                ", fecha_HR_MM_registrada=" + fechaDeCreacion +
                '}';
    }
}
