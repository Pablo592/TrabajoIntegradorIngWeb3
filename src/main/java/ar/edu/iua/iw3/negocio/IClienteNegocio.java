package ar.edu.iua.iw3.negocio;

import java.util.List;

import ar.edu.iua.iw3.modelo.Cliente;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;

public interface IClienteNegocio {

	List<Cliente> listado() throws NegocioException;

	Cliente cargar(long id) throws NegocioException, NoEncontradoException;

	Cliente agregar(Cliente cliente) throws NegocioException, EncontradoException;

	Cliente modificar(Cliente cliente) throws NegocioException, NoEncontradoException;

	void eliminar(long id) throws NegocioException, NoEncontradoException;

}
