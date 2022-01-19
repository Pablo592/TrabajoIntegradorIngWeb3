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
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    @ApiModelProperty(notes = "Una alarma pertenece a una unica orden")
    @ManyToOne(cascade=CascadeType.ALL)
    private Orden ordenAlarma;

    private Date  fechaAceptacion;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public Orden getOrdenAlarma() {
        return ordenAlarma;
    }

    public void setOrdenAlarma(Orden ordenAlarma) {
        this.ordenAlarma = ordenAlarma;
    }

    public Date getFechaAceptacion() {
        return fechaAceptacion;
    }

    public void setFechaAceptacion(Date fechaAceptacion) {
        this.fechaAceptacion = fechaAceptacion;
    }
}
