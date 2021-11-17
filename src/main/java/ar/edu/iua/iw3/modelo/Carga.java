package ar.edu.iua.iw3.modelo;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@Inheritance(strategy=InheritanceType.JOINED)   //obligo a que las clases que hereden de esta clase creen sus propias tablas
public class Carga implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private float masaAcumuladaKg;

    private float densidadProductoKilogramoMetroCub;

    private float temperaturaProductoCelcius;

    private float caudalLitroSegundo;

    public Carga() {
    }

    public Carga(float masaAcumuladaKg, float densidadProductoKilogramoMetroCub, float temperaturaProductoCelcius, float caudalLitroSegundo) {
        this.masaAcumuladaKg = masaAcumuladaKg;
        this.densidadProductoKilogramoMetroCub = densidadProductoKilogramoMetroCub;
        this.temperaturaProductoCelcius = temperaturaProductoCelcius;
        this.caudalLitroSegundo = caudalLitroSegundo;
    }

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
}