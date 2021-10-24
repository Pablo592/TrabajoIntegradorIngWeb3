package ar.edu.iua.iw3.negocio;

import java.util.List;

import ar.edu.iua.iw3.modelo.Chofer;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;

public interface IChoferNegocio {

	List<Chofer> listado() throws NegocioException;

	Chofer cargar(long id) throws NegocioException, NoEncontradoException;

	Chofer agregar(Chofer chofer) throws NegocioException, EncontradoException;

	Chofer modificar(Chofer chofer) throws NegocioException, NoEncontradoException;

	void eliminar(long id) throws NegocioException, NoEncontradoException;

}
