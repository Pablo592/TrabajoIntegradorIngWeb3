package ar.edu.iua.iw3.modelo.dto;

import io.swagger.annotations.ApiModelProperty;

public class ConciliacionDTO {
    @ApiModelProperty(notes = "Peso del camión vacío mas el peso final de la carga (kg).", example = "6000")
    private float pesajeInicial;
    @ApiModelProperty(notes = "Peso del camión vacío mas el peso final de la carga (kg).", example = "6000")
    private float pesajeFinal;
    @ApiModelProperty(notes = "Peso del combustible (kG) ya cargado.", example = "30")
    private float masaAcumuladaKg;
    @ApiModelProperty(notes = "(Peso final - Peso Inicial) del camion .", example = "6000")
    private float netoPorBalanza;
    @ApiModelProperty(notes = "Diferencia entre balanza y caudalímetro: Neto por balanza - Producto cargado.", example = "6000")
    private float diferenciaNetoPorBalanza_masaAcumuludada;
    @ApiModelProperty(notes = "Densidad promedio del combustible (kg/m^3).", example = "0,874")
    private float promedDensidadProductoKilogramoMetroCub;
    @ApiModelProperty(notes = "Temperatura promedio del combustible (°C).", example = "16")
    private float promedioTemperaturaProductoCelcius;
    @ApiModelProperty(notes = "Cantidad promedio de combustible cargado por segundo (litro/segundo).", example = "0,16")
    private float promedioCaudalLitroSegundo;


    public ConciliacionDTO(float pesajeInicial, float pesajeFinal, float masaAcumuladaKg, float netoPorBalanza, float diferenciaNetoPorBalanza_masaAcumuludada, float promedDensidadProductoKilogramoMetroCub, float promedioTemperaturaProductoCelcius, float promedioCaudalLitroSegundo) {
        this.pesajeInicial = pesajeInicial;
        this.pesajeFinal = pesajeFinal;
        this.masaAcumuladaKg = masaAcumuladaKg;
        this.netoPorBalanza = netoPorBalanza;
        this.diferenciaNetoPorBalanza_masaAcumuludada = diferenciaNetoPorBalanza_masaAcumuludada;
        this.promedDensidadProductoKilogramoMetroCub = promedDensidadProductoKilogramoMetroCub;
        this.promedioTemperaturaProductoCelcius = promedioTemperaturaProductoCelcius;
        this.promedioCaudalLitroSegundo = promedioCaudalLitroSegundo;
    }

    public float getPesajeInicial() {
        return pesajeInicial;
    }

    public void setPesajeInicial(float pesajeInicial) {
        this.pesajeInicial = pesajeInicial;
    }

    public float getPesajeFinal() {
        return pesajeFinal;
    }

    public void setPesajeFinal(float pesajeFinal) {
        this.pesajeFinal = pesajeFinal;
    }

    public float getMasaAcumuladaKg() {
        return masaAcumuladaKg;
    }

    public void setMasaAcumuladaKg(float masaAcumuladaKg) {
        this.masaAcumuladaKg = masaAcumuladaKg;
    }

    public float getNetoPorBalanza() {
        return netoPorBalanza;
    }

    public void setNetoPorBalanza(float netoPorBalanza) {
        this.netoPorBalanza = netoPorBalanza;
    }

    public float getDiferenciaNetoPorBalanza_masaAcumuludada() {
        return diferenciaNetoPorBalanza_masaAcumuludada;
    }

    public void setDiferenciaNetoPorBalanza_masaAcumuludada(float diferenciaNetoPorBalanza_masaAcumuludada) {
        this.diferenciaNetoPorBalanza_masaAcumuludada = diferenciaNetoPorBalanza_masaAcumuludada;
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
}
