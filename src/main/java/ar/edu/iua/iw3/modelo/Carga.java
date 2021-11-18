package ar.edu.iua.iw3.modelo;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
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