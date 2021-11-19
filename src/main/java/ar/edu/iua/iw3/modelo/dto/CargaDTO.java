package ar.edu.iua.iw3.modelo.dto;

public class CargaDTO {
    private float masaAcumuladaKg;
    private float promedDensidadProductoKilogramoMetroCub;
    private float promedioTemperaturaProductoCelcius;
    private float promedioCaudalLitroSegundo;

    public CargaDTO(float promedioMasaAcumuladaKg, float promedDensidadProductoKilogramoMetroCub, float promedioTemperaturaProductoCelcius, float promedioCaudalLitroSegundo) {
        this.masaAcumuladaKg = promedioMasaAcumuladaKg;
        this.promedDensidadProductoKilogramoMetroCub = promedDensidadProductoKilogramoMetroCub;
        this.promedioTemperaturaProductoCelcius = promedioTemperaturaProductoCelcius;
        this.promedioCaudalLitroSegundo = promedioCaudalLitroSegundo;
    }

    public float getMasaAcumuladaKg() {
        return masaAcumuladaKg;
    }

    public void setMasaAcumuladaKg(float masaAcumuladaKg) {
        this.masaAcumuladaKg = masaAcumuladaKg;
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
