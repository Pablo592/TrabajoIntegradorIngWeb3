package ar.edu.iua.iw3.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

@Entity
@Table(name = "camion")
public class Camion  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 7, nullable = false, unique = true)
    private String patente;

    @Column(length = 100)
    private String descripcion;

    @Column(columnDefinition = "DOUBLE", nullable = false)
    private double cisternadoLitros;

    @Column(columnDefinition = "DOUBLE", nullable = false)
    private double preset;              //Limite a cargar en el camion

    @Column(columnDefinition = "DOUBLE default 0")
    private double tara;                //Peso del camion vacio
    @Column(columnDefinition = "DOUBLE default 0")
    private double pesoFinalCamion;		//Peso del camion tras cargarse

    @OneToMany(targetEntity = Orden.class, mappedBy = "camion", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Orden> ordenList;

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

    public double getPesoFinalCamion() {
        return pesoFinalCamion;
    }

    public void setPesoFinalCamion(double pesoFinalCamion) {
        this.pesoFinalCamion = pesoFinalCamion;
    }

    public List<Orden> getOrdenList() {
        return ordenList;
    }

    public void setOrdenList(List<Orden> ordenList) {
        this.ordenList = ordenList;
    }
}
