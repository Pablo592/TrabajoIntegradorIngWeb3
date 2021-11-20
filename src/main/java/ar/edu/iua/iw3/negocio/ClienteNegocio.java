package ar.edu.iua.iw3.negocio;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.modelo.Cliente;
import ar.edu.iua.iw3.modelo.persistencia.ClienteRepository;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;

@Service
public class ClienteNegocio implements IClienteNegocio{

	@Autowired
	private ClienteRepository clienteDAO;
	
	
	private Logger log = LoggerFactory.getLogger(ClienteNegocio.class);

	@Override
	public List<Cliente> listado() throws NegocioException {
		try {
			return clienteDAO.findAll();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new NegocioException(e);
		}
	}

	
	@Override
	public Cliente cargar(long id) throws NegocioException, NoEncontradoException {
		Optional<Cliente> o;
		try {
			o = clienteDAO.findById(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new NegocioException(e);
		}
		if (!o.isPresent()) {
			throw new NoEncontradoException("No se encuentra el cliente con id=" + id);
		}
		return o.get();
	}

	@Override
	public Cliente agregar(Cliente cliente) throws NegocioException, EncontradoException {
			try{
				if(findByContacto(cliente.getContacto())!=null)
					throw new EncontradoException("Ya existe el cliente con contacto =" + cliente.getContacto());
				cargar(cliente.getId());
				throw new EncontradoException("Ya existe una cliente con id=" + cliente.getId());
			}catch (NoEncontradoException e){

			}
			try {
				return clienteDAO.save(cliente);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new NegocioException(e);
			}
	}
	

	public Cliente findByContacto(long contacto){
		return clienteDAO.findByContacto(contacto).orElse(null);
	}

	@Override
	public Cliente modificar(Cliente cliente) throws NegocioException, NoEncontradoException {
		try {
			return clienteDAO.save(cliente);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new NegocioException(e);
		}
	}
	

	@Override
	public void eliminar(long id) throws NegocioException, NoEncontradoException {
		cargar(id);
		try {
			clienteDAO.deleteById(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new NegocioException(e);
		}
		
	}
}
