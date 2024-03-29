package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.*;
import ar.edu.iua.iw3.modelo.dto.ConciliacionDTO;
import ar.edu.iua.iw3.modelo.persistencia.OrdenRepository;
import ar.edu.iua.iw3.negocio.excepciones.*;
import ar.edu.iua.iw3.util.MensajeRespuesta;
import ar.edu.iua.iw3.util.RespuestaGenerica;
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
    private ICamionNegocio camionNegocio;
    @Autowired
    private IClienteNegocio clienteNegocio;
    @Autowired
    private IProductoNegocio productoNegocio;
    @Autowired
    private IChoferNegocio choferNegocio;
    @Autowired
    private ICargaNegocio cargaNegocio;

    @Autowired
    private IAlarmaNegocio alarmaNegocio;

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
    public RespuestaGenerica<Orden> frenarCargar(String codigoExterno) throws NegocioException, NoEncontradoException, UnprocessableException {
        Orden ordenBD = findByCodigoExterno(codigoExterno);
        if(null==ordenBD)
            throw new NoEncontradoException("No existe la orden con codigo externo =" + codigoExterno);
        if(ordenBD.getEstado()!=2)
            throw new UnprocessableException("Solo se pueden parar ordes cuyo estado sea 2");
        try{
            MensajeRespuesta m=new MensajeRespuesta();
            RespuestaGenerica<Orden> r = new RespuestaGenerica<Orden>(ordenBD, m);
            ordenBD.setEstado(3);
            Orden ordenNueva = modificar(ordenBD);
            m.setCodigo(0);
            m.setMensaje(ordenNueva.toString());
            return r;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override
    public RespuestaGenerica<Orden> establecerPesajeFinal(Orden orden) throws NegocioException, NoEncontradoException, UnprocessableException {

        Orden ordenBD = findByCodigoExterno(orden.getCodigoExterno());
        if(null==ordenBD)
            throw new NoEncontradoException("No existe la orden con codigo externo =" + orden.getCodigoExterno());
        if(ordenBD.getEstado()!=3)
            throw new UnprocessableException("Solo se puede establecer el pesaje final solo si el estado es 3");
        try{
            camionNegocio.setearPesoFinalCamion(orden);
            MensajeRespuesta m=new MensajeRespuesta();
            RespuestaGenerica<Orden> r = new RespuestaGenerica<Orden>(ordenBD, m);
            ordenBD.setEstado(4);
            ordenBD.getCamion().setPesoFinalCamion(orden.getCamion().getPesoFinalCamion());
            ordenBD.setFechaRecepcionPesajeFinal(new Date());
            Orden ordenNueva = modificar(ordenBD);
            m.setCodigo(0);
            m.setMensaje(ordenNueva.toString());
            return r;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override
    public ConciliacionDTO obtenerConciliacion(String codigoExterno) throws NegocioException, NoEncontradoException, UnprocessableException {

        Orden ordenBD = findByCodigoExterno(codigoExterno);
        if(null==ordenBD)
            throw new NoEncontradoException("No existe la orden con codigo externo =" + codigoExterno);
        if(ordenBD.getEstado()<4)
            throw new UnprocessableException("Solo se puede obtener la consiliacion cuando la orden esta en estado 4");
        try{
            ConciliacionDTO conciliacionDTO = ordenDAO.getPesoInicialAndPesoFinalAndMasaAcumuladaKgAndDiferenciaMasaAcu_DeltaPeso(ordenBD.getId());
            return conciliacionDTO;
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
    public RespuestaGenerica<Orden> agregar(Orden orden) throws NegocioException, EncontradoException, BadRequest, UnprocessableException {
        MensajeRespuesta m=new MensajeRespuesta();
        RespuestaGenerica<Orden> r = new RespuestaGenerica<Orden>(orden, m);

        try {
            if(null!=findByCodigoExterno(orden.getCodigoExterno()))
                throw new EncontradoException("Ya existe en la base de datos una orden con el numero =" + orden.getCodigoExterno());

            cargar(orden.getId());
            throw new EncontradoException("Ya existe una orden con id=" + orden.getId());
        } catch (NoEncontradoException e) {
        }

        Camion camionJson = orden.getCamion();
        Cliente clienteJson = orden.getCliente();
        Chofer choferJson = orden.getChofer();
        Producto productoJson = orden.getProducto();
        validarMetadata(camionJson,clienteJson,choferJson,productoJson);
        convertirMayusculasPatenteCamionYnombreProducto(camionJson,productoJson);


        Cliente cliente = clienteNegocio.findByContacto(clienteJson.getContacto());
        Camion camion = camionNegocio.findCamionByPatente(camionJson.getPatente());
        Chofer chofer = choferNegocio.findByDocumento(choferJson.getDocumento());
        Producto producto = productoNegocio.findProductoByNombre(productoJson.getNombre());

        if (camion != null && camion.isOcupado())
            throw new UnprocessableException("El camion con patente: " + camion.getPatente() + " esta ocupado por el momento, por favor seleccione otro camion");

        if (chofer != null && chofer.isOcupado())
            throw new UnprocessableException("El chofer con documento : " + chofer.getDocumento() + " esta ocupado por el momento, por favor seleccione otro chofer");


        if(camion == null){
            camionJson.setOcupado(true);
            orden.setCamion(camionNegocio.agregar(camionJson));
        }
        else
            orden.setCamion(camion);

        if(cliente == null)
            orden.setCliente(clienteNegocio.agregar(clienteJson).getEntidad());
        else
            orden.setCliente(cliente);

        if(chofer  == null){
            choferJson.setOcupado(true);
            orden.setChofer(choferNegocio.agregar(choferJson));
        }
        else
            orden.setChofer(chofer);

        if(producto == null)
            orden.setProducto(productoNegocio.agregar(productoJson));
        else
            orden.setProducto(producto);

        try {
            orden.setEstado(1);
            Orden orderNueva = ordenDAO.save(orden);

            m.setCodigo(0);
            m.setMensaje(orderNueva.toString());

            return r;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    public Orden findByCodigoExterno( String codigoExterno) {
        return ordenDAO.findByCodigoExterno(codigoExterno).orElse(null);
    }

    private void validarMetadata(Camion camion,Cliente cliente,Chofer chofer,Producto producto) throws BadRequest{
        if(camion.checkBasicData() != null)
            throw new BadRequest(camion.checkBasicData());
        if (cliente.checkBasicData()!= null)
            throw new BadRequest(cliente.checkBasicData());
        if(chofer.checkBasicData()!= null)
            throw new BadRequest(chofer.checkBasicData());
        if(producto.checkBasicData() != null)
            throw new BadRequest(producto.checkBasicData());
    }
    private void convertirMayusculasPatenteCamionYnombreProducto(Camion camionJson,Producto productoJson) {
        camionJson.setPatente(camionJson.getPatente().toUpperCase());
        productoJson.setNombre(productoJson.getNombre().toUpperCase());
    }


    @Override
    public Orden modificar(Orden orden) throws NegocioException, NoEncontradoException, ConflictException {
        cargar(orden.getId());
        Orden ordenBD= findByCodigoExterno(orden.getCodigoExterno());

        MensajeRespuesta m=new MensajeRespuesta();
        RespuestaGenerica<Orden> r = new RespuestaGenerica<Orden>(orden, m);

        if(null!=ordenBD) {

            if (orden.getId() != ordenBD.getId())
                throw new ConflictException("Ya existe la orden " + ordenBD.getId() + "con el numero ="
                        + orden.getCodigoExterno());

            return	saveOrden(orden);
        }

        return saveOrden(orden);
    }

    private  Orden saveOrden(Orden orden) throws NegocioException {
        try {
            return ordenDAO.save(orden);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override
    public RespuestaGenerica<Orden> eliminar(long id) throws NegocioException, NoEncontradoException {
        Orden orden= cargar(id);
        MensajeRespuesta m=new MensajeRespuesta();
        RespuestaGenerica<Orden> r = new RespuestaGenerica<Orden>(orden, m);

        try {
            for (Alarma a:orden.getAlarmaList()) {
                alarmaNegocio.eliminar(a.getId());
            }
            for (Carga c:orden.getCargaList()) {
                cargaNegocio.eliminar(c.getId());
            }
            ordenDAO.deleteById(id);
            m.setCodigo(0);
            m.setMensaje("Se elimino la orden con id: " + id );

            Camion camionDesocupado =  orden.getCamion();
            camionDesocupado.setOcupado(false);
            camionNegocio.modificar(camionDesocupado);

            Chofer choferDesocupado = orden.getChofer();
            choferDesocupado.setOcupado(false);
            choferNegocio.modificar(choferDesocupado);

            return r;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override
    public RespuestaGenerica<Orden> establecerPesajeInicial(Orden orden) throws NegocioException, NoEncontradoException, BadRequest, UnprocessableException, ConflictException {
        MensajeRespuesta m=new MensajeRespuesta();
        RespuestaGenerica<Orden> r = new RespuestaGenerica<Orden>(orden, m);

        validarMetadata(orden);
        Orden ordenBD = findByCodigoExterno(orden.getCodigoExterno());
        if(null==ordenBD)
            throw new NoEncontradoException("No existe la orden con codigo externo =" + orden.getCodigoExterno());

        camionNegocio.setearPesoInicialCamion(orden.getCamion(), ordenBD.getCamion());
        ordenBD = validarFechaPesajeInicial(orden, ordenBD);

        if(ordenBD.getEstado()!=1)
            throw new UnprocessableException("Solo se puede pasar a estado 2 las ordenes en estado 1");

        ordenBD.setEstado(2);
        ordenBD.setPassword(String.valueOf(Math.abs(ordenBD.hashCode())).substring(0,5));
        Orden ordenNueva = modificar(ordenBD);

        m.setCodigo(0);
        m.setMensaje(ordenNueva.toString());
        return r;
    }

    private Orden validarFechaPesajeInicial(Orden orden, Orden ordenDB) throws ConflictException {
        Date fechaPesajeInicialRecibida = orden.getFechaPesajeInicial();

        if(ordenDB.getFechaTurno().compareTo(fechaPesajeInicialRecibida)>0)
            throw new ConflictException("La fecha de pesaje debe de ser despues de la fecha de turno");

        ordenDB.setFechaPesajeInicial(fechaPesajeInicialRecibida);
        return  ordenDB;
    }

    public RespuestaGenerica<Orden> cambiarUmbralTemperatura(Orden orden) throws BadRequest, NoEncontradoException, NegocioException, ConflictException {
        MensajeRespuesta m=new MensajeRespuesta();
        RespuestaGenerica<Orden> r = new RespuestaGenerica<Orden>(orden, m);


        if(orden.getUmbralTemperaturaCombustible() < 1)
            throw new BadRequest("Ingrese un umbral de temperatura valido");

        Orden ordenBD = findByCodigoExterno(orden.getCodigoExterno());

        if(ordenBD == null)
            throw new NoEncontradoException("No existe la orden con el codigo externo: " + orden.getCodigoExterno());

        ordenBD.setUmbralTemperaturaCombustible(orden.getUmbralTemperaturaCombustible());
        modificar(ordenBD);
        return r;
    }

    private void validarMetadata(Orden orden) throws BadRequest {
        String error = orden.checkBasicData();
        if (error != null )
            throw new BadRequest(error);
        error = validarFechaInicialOrden_TaraYPatenteCamion(orden);
        if(error != null)
            throw new BadRequest(error);
    }

    private String validarFechaInicialOrden_TaraYPatenteCamion(Orden orden) {

        if(orden.getFechaPesajeInicial() == null)
            return "El atributo 'fechaPesajeInicial' debe poseer un valor";
        if(orden.getCamion().getPatente() == null)
            return "El atributo 'Patente' es obligatorio";
        if(orden.getCamion().getTara() <1)
            return "El atributo 'Tara' debe poseer un valor valido";
        return null;
    }


}
