package ar.edu.iua.iw3.security;

import ar.edu.iua.iw3.modelo.Cuentas.IUsuarioNegocio;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PersistenceUserDetailService implements UserDetailsService {

	@Autowired
	private IUsuarioNegocio userNegocio;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario user=null;
		
		try {
			user=userNegocio.cargarPorNombreOEmail(username);
		} catch (NegocioException e) {
		
			e.printStackTrace();
		} catch (NoEncontradoException e) {
			throw new UsernameNotFoundException(e.getMessage());
		}
		
		return user;
	}

}
