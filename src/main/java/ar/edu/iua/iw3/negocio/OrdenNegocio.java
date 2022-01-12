package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.*;
import ar.edu.iua.iw3.modelo.dto.ConciliacionDTO;
import ar.edu.iua.iw3.modelo.persistencia.OrdenRepository;
import ar.edu.iua.iw3.negocio.excepciones.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenNegocio implements IOrdenNegocio{

    private Logger log = LoggerFactory.getLogger(OrdenNegocio.class);
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
    @Autowired
    private CargaNegocio cargaNegocio;

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
    public Orden traerUltimaCarga(String codigoExterno) throws NegocioException, NoEncontradoException {

     Carga car =   cargaNegocio.traerUltimaCarga(codigoExterno);
     Orden ord = car.getOrden();
     ord.setCargaList(null);

     return ord;
    }

    @Override
    public Orden frenarCargar(String codigoExterno) throws NegocioException, NoEncontradoException {
        Orden ordenBD = findByCodigoExterno(codigoExterno);
        if(null==ordenBD)
            throw new NoEncontradoException("No existe la orden con codigo externo =" + codigoExterno);
        if(ordenBD.getEstado()!=2)
            throw new NegocioException("Solo se pueden parar ordes cuyo estado sea 2");
        try{
            ordenBD.setEstado(3);
            return modificar(ordenBD);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override
    public Orden establecerPesajeFinal(Orden orden) throws NegocioException, NoEncontradoException {
        Orden ordenBD = findByCodigoExterno(orden.getCodigoExterno());
        if(null==ordenBD)
            throw new NoEncontradoException("No existe la orden con codigo externo =" + orden.getCodigoExterno());
        if(ordenBD.getEstado()!=3)
            throw new NegocioException("Solo se puede establecer el pesaje final solo si el estado es 3");
        try{
            camionNegocio.setearPesoFinalCamion(orden);
            ordenBD.setEstado(4);
            ordenBD.setFechaRecepcionPesajeFinal(new Date());
            return modificar(ordenBD);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override
    public ConciliacionDTO obtenerConciliacion(String codigoExterno) throws NegocioException, NoEncontradoException {
        Orden ordenBD = findByCodigoExterno(codigoExterno);
        if(null==ordenBD)
            throw new NoEncontradoException("No existe la orden con codigo externo =" + codigoExterno);
        if(ordenBD.getEstado()<3)
            throw new NegocioException("Solo se puede obtener la consiliacion cuando la orden esta en estado 3 o 4");
        try{
            return ordenDAO.getPesoInicialAndPesoFinalAndMasaAcumuladaKgAndDiferenciaMasaAcu_DeltaPeso(ordenBD.getId());
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
    public Orden agregar(Orden orden) throws NegocioException, EncontradoException, BadRequest {
        try {
            if(null!=findByCodigoExterno(orden.getCodigoExterno()))
                throw new EncontradoException("Ya existe en la base de datos una orden con el numero =" + orden.getCodigoExterno());
            //busco la orden
            cargar(orden.getId()); 		// tira excepcion sino no lo encuentra
            throw new EncontradoException("Ya existe una orden con id=" + orden.getId());
        } catch (NoEncontradoException e) {
        }
        //1.0 --> valido si la metadata de cada campo es el correcto
        Camion camionJson = orden.getCamion();
        Cliente clienteJson = orden.getCliente();
        Chofer choferJson = orden.getChofer();
        Producto productoJson = orden.getProducto();
        validarMetadata(camionJson,clienteJson,choferJson,productoJson);
        convertirMayusculasPatenteCamionYnombreProducto(camionJson,productoJson);
        try {
            //2.0 --> Los busco en la bd
            Camion camion = camionNegocio.findCamionByPatente(camionJson.getPatente());
            Cliente cliente = clienteNegocio.findByContacto(clienteJson.getContacto());
            Chofer chofer = choferNegocio.findByDocumento(choferJson.getDocumento());
            Producto producto = productoNegocio.findProductoByNombre(productoJson.getNombre());

            if(camion!= null)
                orden.setCamion(camion);
            if(cliente!= null)
                orden.setCliente(cliente);
            if(chofer!= null)
                orden.setChofer(chofer);
            if(producto != null)
                orden.setProducto(producto);
            ////3.0 --> Los creo en caso de que no existan
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

    private void validarMetadata(Camion camion,Cliente cliente,Chofer chofer,Producto producto) throws BadRequest{
        if(camion.checkBasicData() != null
            || cliente.checkBasicData()!= null
            || chofer.checkBasicData()!= null
            || producto.checkBasicData() != null)
            throw new BadRequest(camion.checkBasicData());
    }
    private void convertirMayusculasPatenteCamionYnombreProducto(Camion camionJson,Producto productoJson) {
        camionJson.setPatente(camionJson.getPatente().toUpperCase());
        productoJson.setNombre(productoJson.getNombre().toUpperCase());
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
    public Orden establecerPesajeInicial(Orden orden) throws NegocioException, NoEncontradoException, BadRequest, ConflictException {
        Orden ordenBD = findByCodigoExterno(orden.getCodigoExterno());
        if(null==ordenBD)
            throw new NoEncontradoException("No existe la orden con codigo externo =" + orden.getCodigoExterno());

        camionNegocio.setearPesoIni(orden.getCamion(), ordenBD.getCamion());
        ordenBD = validarFechaPesajeInicial(orden, ordenBD);
        ordenBD.setEstado(2);
        ordenBD.setPassword(String.valueOf(Math.abs(ordenBD.hashCode())).substring(0,5));
    return modificar(ordenBD);

    }

    private Orden validarFechaPesajeInicial(Orden orden, Orden ordenDB) throws ConflictException {
        Date fechaPesajeInicialRecibida = orden.getFechaPesajeInicial();

        if(ordenDB.getFechaTurno().compareTo(fechaPesajeInicialRecibida)>0)
            throw new ConflictException("La fecha de pesaje debe de ser despues de la fecha de turno");

        ordenDB.setFechaPesajeInicial(fechaPesajeInicialRecibida);
        return  ordenDB;
    }




}
