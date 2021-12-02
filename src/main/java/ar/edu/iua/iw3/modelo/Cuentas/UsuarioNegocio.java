package ar.edu.iua.iw3.modelo.Cuentas;

import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioNegocio implements IUsuarioNegocio {

	@Autowired
	private UserRepository userDAO;

	@Override
	public Usuario cargar(int id) throws NegocioException, NoEncontradoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Usuario> lista() throws NegocioException {
		try {
			return userDAO.findAll();
		} catch (Exception e) {
			throw new NegocioException(e);
		}
	}

	@Override
	public Usuario agregar(Usuario usuario) throws NegocioException, EncontradoException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Usuario modificar(Usuario usuario) throws NegocioException, EncontradoException, NoEncontradoException {
		try {
			return userDAO.save(usuario);
		} catch (Exception e) {
			throw new NegocioException(e);
		}
	}

	@Override
	public Usuario cargarPorNombreOEmail(String nombreOEmail) throws NegocioException, NoEncontradoException {
		Optional<Usuario> o = null;
		try {
			o = userDAO.findFirstByUsernameOrEmail(nombreOEmail, nombreOEmail);
		} catch (Exception e) {
			throw new NegocioException(e);
		}

		if (!o.isPresent())
			throw new NoEncontradoException(
					String.format("No se encuentra un user con nombre o email = '%s'", nombreOEmail));
		
		return o.get();
	}

}
