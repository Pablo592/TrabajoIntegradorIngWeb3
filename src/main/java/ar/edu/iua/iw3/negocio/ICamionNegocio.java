package ar.edu.iua.iw3.negocio;

import java.util.List;

import ar.edu.iua.iw3.modelo.Camion;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.negocio.excepciones.*;

public interface ICamionNegocio {

	List<Camion> listado() throws NegocioException;

	Camion cargar(long id) throws NegocioException, NoEncontradoException;

	Camion agregar(Camion camion) throws NegocioException, EncontradoException, BadRequest;

	Camion modificar(Camion camion) throws NegocioException, NoEncontradoException;

	void eliminar(long id) throws NegocioException, NoEncontradoException;

	public Camion setearPesoFinalCamion(Orden orden) throws NoEncontradoException, NegocioException;

    Camion setearPesoInicialCamion(Camion camionRecibido, Camion camionBD ) throws NoEncontradoException, NegocioException, BadRequest, ConflictException;

	public Camion findCamionByPatente(String patente);

}
