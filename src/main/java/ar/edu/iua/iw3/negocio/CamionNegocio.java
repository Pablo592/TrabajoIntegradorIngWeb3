package ar.edu.iua.iw3.negocio;

import java.util.List;
import java.util.Optional;

import ar.edu.iua.iw3.modelo.Camion;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.modelo.persistencia.OrdenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.modelo.persistencia.CamionRepository;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;


@Service
public class CamionNegocio implements ICamionNegocio{
	
	private Logger log = LoggerFactory.getLogger(CamionNegocio.class);

	@Autowired
	private CamionRepository camionDAO;

	@Autowired
	private CargaNegocio cargaNegocio;
	
	@Override
	public List<Camion> listado() throws NegocioException {
		try {
			return camionDAO.findAll();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new NegocioException(e);
		}
	}


	@Override
	public Camion cargar(long id) throws NegocioException, NoEncontradoException {
		Optional<Camion> o;
		try {
			o = camionDAO.findById(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new NegocioException(e);
		}
		if (!o.isPresent()) {
			throw new NoEncontradoException("No se encuentra el camion con id=" + id);
		}
		return o.get();
	}

	
	@Override
	public Camion agregar(Camion camion) throws NegocioException, EncontradoException {
		try {
			   if(null!=findCamionByPatente(camion.getPatente()))
			        throw new EncontradoException("Ya existe un camion con la patente =" + camion.getPatente());
				cargar(camion.getId()); 									// tira excepcion sino no lo encuentra
				throw new EncontradoException("Ya existe un camion con id=" + camion.getId());
			} catch (NoEncontradoException e) {
			}
			try {
				return camionDAO.save(camion);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new NegocioException(e);
			}
	}
	
	public Camion findCamionByPatente(String patente) {
		Optional<Camion> o = camionDAO.findByPatente(patente);
		if(o.isPresent())
			return  o.get();
		return null;
	}

	
	@Override
	public Camion modificar(Camion camion) throws NegocioException, NoEncontradoException {
				//Paso 1: busco existencia del id del camion	
				//Paso 2: busco existencia de patente duplicada 	
				//Paso 3_a:si la patente del camion esta asignado a un camion con diferente id del que se quiere modificar entonces tengo se genera un error
				//Paso 3_b:si la patente del camion esta asociada al mismo id del camion ya registrado entonces no se debe de generar error
				//Paso 4: Si ningun camion tiene asignada la patente se lo debe de modiicar sin problemas
				
				cargar(camion.getId()); //Paso 1
				Camion camionWithPatente = findCamionByPatente(camion.getPatente());
				
				if(null!=camionWithPatente) { //Paso 2 
					
					if (camion.getId() != camionWithPatente.getId()) 
						throw new NegocioException("Ya existe el camion " + camionWithPatente.getId() + "con la patente ="
								+ camion.getPatente());	//Paso 3_a
					
					return	saveCamion(camion);	//Paso 3_b
				}
				
				return saveCamion(camion);	//Paso 4
	}
	
	public  Camion saveCamion(Camion componente) throws NegocioException {
		try {
			return camionDAO.save(componente); // sino existe el camion lo cargo
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new NegocioException(e);
		}
	}
	
	
	@Override
	public void eliminar(long id) throws NegocioException, NoEncontradoException {
		cargar(id);
		try {
			camionDAO.deleteById(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new NegocioException(e);
		}

	}

	@Override
	public Camion setearPesoIni(Camion camionRecibido, Camion camionBD) throws NoEncontradoException, NegocioException {
			if(!camionBD.getPatente().equals(camionRecibido.getPatente()))
				throw new NegocioException("La patente enviada :"+ camionRecibido.getPatente()+" no esta asociada a la orden enviada");
			if(camionRecibido.getTara()<= 0)
				throw new NegocioException("La tara no puede ser negativa");
			camionBD.setTara(camionRecibido.getTara());

			return modificar(camionBD);
	}

	public Camion setearPesoFinalCamion(Orden orden) throws NoEncontradoException, NegocioException {
		Camion camionBD = cargar( orden.getCamion().getId());	//validarlo sino colocar que busque por dni
		double pesoInicial = camionBD.getTara();
		double pesoFinal = cargaNegocio.traerUltimaCarga(orden.getCodigoExterno()).getMasaAcumuladaKg();
		camionBD.setPesoFinalCamion(pesoInicial+pesoFinal);
		return modificar(camionBD);
	}

}
