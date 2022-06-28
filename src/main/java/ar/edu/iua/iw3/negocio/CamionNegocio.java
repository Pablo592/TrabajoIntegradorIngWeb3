package ar.edu.iua.iw3.negocio;

import java.util.List;
import java.util.Optional;

import ar.edu.iua.iw3.modelo.Camion;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.negocio.excepciones.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.iua.iw3.modelo.persistencia.CamionRepository;


@Service
public class CamionNegocio implements ICamionNegocio{
	
	private Logger log = LoggerFactory.getLogger(CamionNegocio.class);

	@Autowired
	private CamionRepository camionDAO;

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
	public Camion agregar(Camion camion) throws NegocioException, EncontradoException, BadRequest {
		String msjCheck = camion.checkBasicData();
		if(msjCheck!=null){
			System.out.println("Hubo un error :"+ msjCheck);
			throw new BadRequest();
		}
		else {
			if (null != findCamionByPatente(camion.getPatente()))
				throw new EncontradoException("Ya existe un camion con id=" + camion.getId());

			try {
				return camionDAO.save(camion);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new NegocioException(e);
			}
		}
	}
	
	public Camion findCamionByPatente(String patente) {
		Optional<Camion> o = camionDAO.findByPatente(patente);
		if(o.isPresent())
			return o.get();
		return null;
	}

	
	@Override
	public Camion modificar(Camion camion) throws NegocioException, NoEncontradoException {
				cargar(camion.getId());
				Camion camionWithPatente = findCamionByPatente(camion.getPatente());
				
				if(null!=camionWithPatente) {
					
					if (camion.getId() != camionWithPatente.getId()) 
						throw new NegocioException("Ya existe el camion " + camionWithPatente.getId() + "con la patente ="
								+ camion.getPatente());	//Paso 3_a
					
					return	saveCamion(camion);
				}
				
				return saveCamion(camion);
	}
	
	public  Camion saveCamion(Camion camion) throws NegocioException {
		try {
			return camionDAO.save(camion);
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

	public Camion setearPesoIni(Camion camionRecibido, Camion camionBD) throws  NegocioException, BadRequest, ConflictException {
			if(!camionBD.getPatente().equalsIgnoreCase(camionRecibido.getPatente()))
				throw new ConflictException("La patente enviada :"+ camionRecibido.getPatente()+" no esta asociada a la orden enviada");
			if(camionRecibido.getTara()<= 0)
				throw new BadRequest("El atributo 'tara' tiene que ser mayor a cero");
			camionBD.setTara(camionRecibido.getTara());
			return saveCamion(camionBD);
	}

	public Camion setearPesoFinalCamion(Orden orden) throws NoEncontradoException, NegocioException {
		Camion camionBD = findCamionByPatente(orden.getCamion().getPatente());

		camionBD.setPesoFinalCamion(orden.getCamion().getPesoFinalCamion());

		return modificar(camionBD);
	}

}
