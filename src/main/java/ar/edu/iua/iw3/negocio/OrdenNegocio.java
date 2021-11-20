package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.*;
import ar.edu.iua.iw3.modelo.persistencia.OrdenRepository;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenNegocio implements IOrdenNegocio{

    @Autowired
    private OrdenRepository ordenDAO;

    @Autowired
    private CamionNegocio camionNegocio;
    @Autowired
    private ClienteNegocio clienteNegocio;
    @Autowired
    private ProductoNegocio productoNegocio;
    @Autowired
    private ChoferNegocio choferNegocio;

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
    public Orden agregar(Orden orden) throws NegocioException, EncontradoException {
        try {
            if(null!=findByCodigoExterno(orden.getCodigoExterno()))
                throw new EncontradoException("Ya existe en la base de datos una orden con el numero =" + orden.getCodigoExterno());
            //busco la orden
            cargar(orden.getId()); 		// tira excepcion sino no lo encuentra
            throw new EncontradoException("Ya existe una orden con id=" + orden.getId());
        } catch (NoEncontradoException e) {
        }
        try {
            Camion camion = camionNegocio.findCamionByPatente(orden.getCamion().getPatente());
            Cliente cliente = clienteNegocio.findByContacto(orden.getCliente().getContacto());
            Chofer chofer = choferNegocio.findByDocumento(orden.getChofer().getDocumento());
            Producto producto = productoNegocio.findProductoByNombre(orden.getProducto().getNombre());
            //si es nulo implica que es un camion, cliente, chofer o producto nuevo
            if(camion!= null)
                orden.setCamion(camion);
            if(cliente!= null)
                orden.setCliente(cliente);
            if(chofer!= null)
                orden.setChofer(chofer);
            if(producto != null)
                orden.setProducto(producto);
            orden.setEstado(1);
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

    @Override
    public Orden establecerPesajeInicial(Orden orden) throws NegocioException, NoEncontradoException {
        Orden ordenBD = findByCodigoExterno(orden.getCodigoExterno());
        if(null==ordenBD)
            throw new NoEncontradoException("No existe la orden con codigo externo =" + orden.getCodigoExterno());

        try {
            if(null== ordenBD)
                throw new NoEncontradoException("La orden "+orden.getCodigoExterno()+" no existe");

            camionNegocio.setearPesoIni(orden.getCamion(), ordenBD.getCamion());
            ordenBD = validarFechaPesajeInicial(orden, ordenBD);
            ordenBD.setEstado(2);
            ordenBD.setPassword(String.valueOf(Math.abs(ordenBD.hashCode())).substring(0,5));
            return modificar(ordenBD);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    private Orden validarFechaPesajeInicial(Orden orden, Orden ordenDB) throws NegocioException {
        Date fechaPesajeInicialRecibida = orden.getFechaPesajeInicial();
        if(ordenDB.getFechaTurno().compareTo(fechaPesajeInicialRecibida)>0)
            throw new NegocioException("La fecha de pesaje debe de ser despues de la fecha de turno");

        ordenDB.setFechaPesajeInicial(fechaPesajeInicialRecibida);
        return  ordenDB;
    }




}
