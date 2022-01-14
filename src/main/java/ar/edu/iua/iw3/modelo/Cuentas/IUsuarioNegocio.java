package ar.edu.iua.iw3.modelo.Cuentas;

import ar.edu.iua.iw3.negocio.excepciones.BadRequest;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;

import java.util.List;

public interface IUsuarioNegocio {

	public Usuario cargar(int id) throws NegocioException, NoEncontradoException;
	public List<Usuario> lista() throws NegocioException;
	public Usuario agregar(Usuario usuario) throws NegocioException, EncontradoException, BadRequest;
	public Usuario modificar(Usuario usuario) throws NegocioException, EncontradoException, NoEncontradoException, BadRequest;
	
	public Usuario cargarPorNombreOEmail(String nombreOEmail) throws NegocioException, NoEncontradoException;

	public void eliminar(int id) throws NegocioException, NoEncontradoException;
}
