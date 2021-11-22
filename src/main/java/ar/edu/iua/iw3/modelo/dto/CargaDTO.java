package ar.edu.iua.iw3.modelo.dto;

import io.swagger.annotations.ApiModelProperty;

public class CargaDTO {
    @ApiModelProperty(notes = "Densidad promedio del combustible (kg/m^3).", example = "0,874")
    private float promedDensidadProductoKilogramoMetroCub;
    @ApiModelProperty(notes = "Temperatura promedio del combustible (°C).", example = "16")
    private float promedioTemperaturaProductoCelcius;
    @ApiModelProperty(notes = "Cantidad promedio de combustible cargado por segundo (litro/segundo).", example = "0,16")
    private float promedioCaudalLitroSegundo;

    public CargaDTO(){}

    public CargaDTO(float promedDensidadProductoKilogramoMetroCub, float promedioTemperaturaProductoCelcius, float promedioCaudalLitroSegundo) {
        this.promedDensidadProductoKilogramoMetroCub = promedDensidadProductoKilogramoMetroCub;
        this.promedioTemperaturaProductoCelcius = promedioTemperaturaProductoCelcius;
        this.promedioCaudalLitroSegundo = promedioCaudalLitroSegundo;
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
