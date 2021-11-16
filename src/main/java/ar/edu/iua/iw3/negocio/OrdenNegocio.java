package ar.edu.iua.iw3.negocio;

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
    public Orden resumenFinal(long id) throws NegocioException, NoEncontradoException {
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
            if(null!=findByNumeroOrden(orden.getNumeroDeOrden()))
                throw new EncontradoException("Ya existe una orden con el numero =" + orden.getNumeroDeOrden());
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

    public Orden findByNumeroOrden( String numeroOrden) {
        return ordenDAO.findByNumeroDeOrden(numeroOrden).orElse(null);
    }


    @Override
    public Orden modificar(Orden orden) throws NegocioException, NoEncontradoException {
        cargar(orden.getId()); //Paso 1
        Orden ordenWithNumeroOrden = findByNumeroOrden(orden.getNumeroDeOrden());

        if(null!=ordenWithNumeroOrden) { //Paso 2

            if (orden.getId() != ordenWithNumeroOrden.getId())
                throw new NegocioException("Ya existe la orden " + ordenWithNumeroOrden.getId() + "con el numero ="
                        + orden.getNumeroDeOrden());	//Paso 3_a

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
