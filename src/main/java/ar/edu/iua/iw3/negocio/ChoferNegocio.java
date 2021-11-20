package ar.edu.iua.iw3.negocio;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.modelo.Chofer;
import ar.edu.iua.iw3.modelo.persistencia.ChoferRepository;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;

@Service
public class ChoferNegocio implements IChoferNegocio{

	@Autowired
	private ChoferRepository choferDAO;

	private Logger log = LoggerFactory.getLogger(ChoferNegocio.class);

		@Override
		public List<Chofer> listado() throws NegocioException {
			try {
				return choferDAO.findAll();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new NegocioException(e);
			}
		}

		
		@Override
		public Chofer cargar(long id) throws NegocioException, NoEncontradoException {
			Optional<Chofer> o;
			try {
				o = choferDAO.findById(id);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new NegocioException(e);
			}
			if (!o.isPresent()) {
				throw new NoEncontradoException("No se encuentra el chofer con id=" + id);
			}
			return o.get();
		}

		@Override
		public Chofer agregar(Chofer chofer) throws NegocioException, EncontradoException {
			try {
				   if(null!=findByDocumento(chofer.getDocumento()))
				        throw new EncontradoException("Ya existe un chofer con el documento =" + chofer.getDocumento());
					cargar(chofer.getId()); 									// tira excepcion sino no lo encuentra
					throw new EncontradoException("Ya existe un chofer con id=" + chofer.getId());
				} catch (NoEncontradoException e) {
				}
				try {
					return choferDAO.save(chofer);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					throw new NegocioException(e);
				}
		}
		
		public Chofer findByDocumento(long documento) {
			return choferDAO.findByDocumento(documento).orElse(null);
		}
		


		@Override
		public Chofer modificar(Chofer chofer) throws NegocioException, NoEncontradoException {
					//Paso 1: busco existencia del id del chofer	
					//Paso 2: busco existencia del documento duplicado 	
					//Paso 3_a:si el documento del chofer esta asignado a un chofer con diferente id del que se quiere modificar entonces tengo se genera un error
					//Paso 3_b:si el documento del chofer esta asociada al mismo id del chofer ya registrado entonces no se debe de generar error
					//Paso 4: Si ningun chofer tiene asignado el documento se lo debe de modiicar sin problemas
					
					cargar(chofer.getId()); //Paso 1
					Chofer choferWithDocumento = findByDocumento(chofer.getDocumento());
					
					if(null!=choferWithDocumento) { //Paso 2 
						
						if (chofer.getId() != choferWithDocumento.getId()) 
							throw new NegocioException("Ya existe el chofer " + choferWithDocumento.getId() + "con el documento ="
									+ chofer.getDocumento());	//Paso 3_a
						
						return	saveChofer(chofer);	//Paso 3_b
					}
					
					return saveChofer(chofer);	//Paso 4
		}
		
		private  Chofer saveChofer(Chofer componente) throws NegocioException {
			try {
				return choferDAO.save(componente); // sino existe el chofer lo cargo
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new NegocioException(e);
			}
		}
		
	
		@Override
		public void eliminar(long id) throws NegocioException, NoEncontradoException {
			cargar(id);
			try {
				choferDAO.deleteById(id);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new NegocioException(e);
			}
			
		}
}
