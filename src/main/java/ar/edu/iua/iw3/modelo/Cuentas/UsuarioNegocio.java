package ar.edu.iua.iw3.modelo.Cuentas;

import ar.edu.iua.iw3.negocio.excepciones.BadRequest;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioNegocio implements IUsuarioNegocio {

	private Logger log = LoggerFactory.getLogger(UsuarioNegocio.class);

	@Autowired
	private UsuarioRepository userDAO;

	@Autowired
	private PasswordEncoder pwdEncoder;

	@Override
	public Usuario cargar(int id) throws NegocioException, NoEncontradoException {
		Optional<Usuario> o;
		try {
			o = userDAO.findById(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new NegocioException(e);
		}
		if (!o.isPresent()) {
			throw new NoEncontradoException("No se encuentra al usuario con id= " + id);
		}
		return o.get();
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
	public Usuario agregar(Usuario usuario) throws NegocioException, EncontradoException, BadRequest {
		//si es nulo esta bien
		String msjCheck = usuario.checkBasicData();
		if (msjCheck != null) {
			throw new BadRequest(msjCheck);
		} else {
			if(usuario.getEmail() != null)
				if (userDAO.findFirstByEmail(usuario.getEmail()).isPresent() )
					throw new EncontradoException("El email " + usuario.getEmail() + " ya se encuentra registrado");

			if (userDAO.findFirstByUsername(usuario.getUsername()).isPresent())
				throw new EncontradoException("El username " + usuario.getUsername() + " ya se encuentra registrado");
			try {
					usuario.setPassword(pwdEncoder.encode(usuario.getPassword()));
					return userDAO.save(usuario);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					throw new NegocioException(e);
				}
		}
		}



	@Override
	public Usuario modificar(Usuario usuario) throws NegocioException, EncontradoException, NoEncontradoException, BadRequest {

		Optional<Usuario> buscandoDb;

		String msjCheck = usuario.checkBasicData();
		if (msjCheck != null) 
			throw new BadRequest(msjCheck);
		
		Usuario usuarioDb = cargar(usuario.getId());

		if(usuarioDb == null) {
			throw new NoEncontradoException("El usuario que desea modificar no se encuentra registrado");
		}else {
			if(usuario.getEmail() != null) {
				buscandoDb = userDAO.findFirstByUsername(usuario.getEmail());
				if (buscandoDb.isPresent())
					if (usuario.getId() != buscandoDb.get().getId())
						throw new EncontradoException("El email " + usuario.getEmail() + " ya se encuentra registrado");
			}
				buscandoDb	= userDAO.findFirstByUsername(usuario.getUsername());
			if(buscandoDb.isPresent())
			if (usuario.getId() != buscandoDb.get().getId())
				throw new EncontradoException("El username " + usuario.getUsername() + " ya se encuentra registrado");
		}
		return saveUser(usuario);	//Paso 4
	}

	public  Usuario saveUser(Usuario usuario) throws NegocioException {
		try {
			usuario.setPassword(pwdEncoder.encode(usuario.getPassword()));
			return userDAO.save(usuario);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
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

	public void eliminar(int id) throws NegocioException, NoEncontradoException {
		cargar(id);
		try {
			userDAO.deleteById(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new NegocioException(e);
		}
	}

}
