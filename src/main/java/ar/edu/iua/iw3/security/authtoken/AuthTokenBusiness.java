package ar.edu.iua.iw3.security.authtoken;

import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthTokenBusiness implements IAuthTokenBusiness {

	@Autowired
	private AuthTokenRespository authTokenDAO;

	@Override
	public AuthToken save(AuthToken at) throws NegocioException {
		try {
			return authTokenDAO.save(at);
		} catch (Exception e) {
			throw new NegocioException(e);
		}
	}

	@Override
	public AuthToken load(String series) throws NegocioException, NoEncontradoException {
		Optional<AuthToken> atO;
		try {
			atO = authTokenDAO.findById(series);
		} catch (Exception e) {
			throw new NegocioException(e);
		}
		if (!atO.isPresent())
			throw new NoEncontradoException("No se encuentra el token de autenticación serie=" + series);
		return atO.get();
	}

	@Override
	public void delete(AuthToken at) throws NegocioException {
		try {
			authTokenDAO.delete(at);
		} catch (Exception e) {
			// throw new ServiceException(e);
		}

	}

	@Override
	public void purgeTokens() throws NegocioException {
		try {
			authTokenDAO.purgeToDate(new Date());
			authTokenDAO.purgeDefault(new Date());
			authTokenDAO.purgeFromToDate(new Date());
			authTokenDAO.purgeRequestLimit();
		} catch (Exception e) {
			throw new NegocioException(e);
		}

	}

	@Override
	public void delete(String token) throws NegocioException {
		try {
			String serie = AuthToken.decode(token)[0];
			authTokenDAO.deleteById(serie);
		} catch (Exception e) {
			throw new NegocioException(e);
		}

	}

}
