package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Camion;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.modelo.persistencia.OrdenRepository;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenNegocio implements IOrdenNegocio{

    @Autowired
    private OrdenRepository ordenDAO;

    @Autowired
    private CamionNegocio camionNegocio;


    private Logger log = LoggerFactory.getLogger(OrdenNegocio.class);

    @Override
    public List<Orden> listado() throws NegocioException {
        try {
            return ordenDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

public Orden establecerPesajeInicial(Orden orden) throws NegocioException{

    Optional<Orden> o;
    try {
        o = verificarExistenciaCamion(orden.getCamion().getPatente());
        if(!o.isPresent())
            throw new NoEncontradoException("Camion no registrado");

        Optional<Camion> ocamion;
        try {
            ocamion = Optional.ofNullable(camionNegocio.setearPesoIni(orden.getCamion()));
            if(!o.isPresent())
                throw new NoEncontradoException("Camion no registrado");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    } catch (Exception e) {
        log.error(e.getMessage(), e);
        throw new NegocioException(e);
    }

    Orden ordenGuardada = o.get();

    ordenGuardada.setFase(2);
    ordenGuardada.setFechaPesajeInicial(orden.getFechaPesajeInicial());
    return saveOrden(orden);
}



    private Optional<Orden> verificarExistenciaCamion(String patente) throws NegocioException{
        Optional<Orden> o;
        try {
        o = ordenDAO.findByCamionPatente(patente);
        if(!o.isPresent())
            throw new NoEncontradoException("No hay orden que tenga un camion registrado con la patente: " + patente);
            return o;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }



    @Override
    public Orden cargar(long id) throws NegocioException, NoEncontradoException {
        Optional<Orden> o;
        try {
            o = ordenDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
        if (!o.isPresent()) {
            throw new NoEncontradoException("No se encuentra la orden con id=" + id);
        }
        return o.get();
    }


    @Override
    public Orden agregar(Orden orden) throws NegocioException, EncontradoException {
        try {
            if(null!=findByCodigoExterno(orden.getCodigoExterno()))
                throw new EncontradoException("Ya existe en la base de datos una orden con el numero =" + orden.getCodigoExterno());
            cargar(orden.getId()); 		// tira excepcion sino no lo encuentra
            throw new EncontradoException("Ya existe una orden con id=" + orden.getId());
        } catch (NoEncontradoException e) {
        }
        try {
            return ordenDAO.save(orden);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    public Orden findByCodigoExterno( String codigoExterno) {
        return ordenDAO.findByCodigoExterno(codigoExterno).orElse(null);
    }

    @Override
    public Orden modificar(Orden orden) throws NegocioException, NoEncontradoException {
        cargar(orden.getId()); //Paso 1
        Orden ordenWithCodigoExterno= findByCodigoExterno(orden.getCodigoExterno());

        if(null!=ordenWithCodigoExterno) { //Paso 2

            if (orden.getId() != ordenWithCodigoExterno.getId())
                throw new NegocioException("Ya existe la orden " + ordenWithCodigoExterno.getId() + "con el numero ="
                        + orden.getCodigoExterno());	//Paso 3_a

            return	saveOrden(orden);	//Paso 3_b
        }

        return saveOrden(orden);	//Paso 4
    }

    private  Orden saveOrden(Orden orden) throws NegocioException {
        try {
            return ordenDAO.save(orden); // sino existe la orden la cargo
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override
    public void eliminar(long id) throws NegocioException, NoEncontradoException {
        cargar(id);
        try {
            ordenDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }
}
