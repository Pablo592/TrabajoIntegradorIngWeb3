package ar.edu.iua.iw3.security.authtoken;

import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;

public interface IAuthTokenBusiness {
	public AuthToken save(AuthToken at) throws NegocioException;

	public AuthToken load(String series) throws NegocioException, NoEncontradoException;

	public void delete(AuthToken at) throws NegocioException;

	public void purgeTokens() throws NegocioException;
	
	public void delete(String token) throws NegocioException ;

}
