package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Alarma;

public interface IGraphNegocio {
	public void pushGraphDataCarga(double preset,float cargaAcumulada,String codigoExterno);
	public void pushExistAlarma(Alarma alarma);

}
