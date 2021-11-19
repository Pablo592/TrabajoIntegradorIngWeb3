package ar.edu.iua.iw3.modelo.dto;

public class CargaDTO {
    private float promedioMasaAcumuladaKg;
    private float promedDensidadProductoKilogramoMetroCub;
    private float promedioTemperaturaProductoCelcius;
    private float promedioCaudalLitroSegundo;

    public CargaDTO(float promedioMasaAcumuladaKg, float promedDensidadProductoKilogramoMetroCub, float promedioTemperaturaProductoCelcius, float promedioCaudalLitroSegundo) {
        this.promedioMasaAcumuladaKg = promedioMasaAcumuladaKg;
        this.promedDensidadProductoKilogramoMetroCub = promedDensidadProductoKilogramoMetroCub;
        this.promedioTemperaturaProductoCelcius = promedioTemperaturaProductoCelcius;
        this.promedioCaudalLitroSegundo = promedioCaudalLitroSegundo;
    }

    public float getPromedioMasaAcumuladaKg() {
        return promedioMasaAcumuladaKg;
    }

    public void setPromedioMasaAcumuladaKg(float promedioMasaAcumuladaKg) {
        this.promedioMasaAcumuladaKg = promedioMasaAcumuladaKg;
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
