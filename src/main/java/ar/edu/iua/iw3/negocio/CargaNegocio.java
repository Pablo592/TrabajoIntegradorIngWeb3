package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.eventos.CargaEvent;
import ar.edu.iua.iw3.modelo.*;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.modelo.dto.CargaDTO;
import ar.edu.iua.iw3.modelo.persistencia.CargaRepository;
import ar.edu.iua.iw3.negocio.excepciones.*;
import ar.edu.iua.iw3.util.MensajeRespuesta;
import ar.edu.iua.iw3.util.RespuestaGenerica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import java.util.*;

@Service
public class CargaNegocio implements ICargaNegocio {

    private Logger log = LoggerFactory.getLogger(CamionNegocio.class);

    private Date proximoTiempoLimite;

    @Autowired
    private CargaRepository cargaDAO;

    @Autowired
    private OrdenNegocio ordenNegocio;

    @Autowired
    private AlarmaNegocio alarmaNegocio;

    @Autowired
    private ApplicationEventPublisher appEventPublisher;

    @Override
    public List<Carga> listado() throws NegocioException {
        try {
            return cargaDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }


    @Override
    public Carga traerUltimaCarga(String codigoExterno) throws NegocioException, NoEncontradoException {
        Orden orden = ordenNegocio.findByCodigoExterno(codigoExterno);
        if (null == orden)
            throw new NoEncontradoException("El codigo externo no pertenece a ninguna orden");
        try {
            return cargaDAO.findTheLastCarga(orden.getId());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }

    }

    @Override
    public Carga cargar(long id) throws NegocioException, NoEncontradoException {
        Optional<Carga> o;
        try {
            o = cargaDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
        if (!o.isPresent()) {
            throw new NoEncontradoException("No se encuentra la carga con id=" + id);
        }
        return o.get();
    }

    @Override
    public RespuestaGenerica<Carga> agregar(Carga carga) throws NegocioException, NoEncontradoException, BadRequest, UnprocessableException, ConflictException, EncontradoException {
        MensajeRespuesta m=new MensajeRespuesta();
        RespuestaGenerica<Carga> r = new RespuestaGenerica<Carga>(carga, m);

        validarMetadata(carga);
        String codigoExterno = carga.getOrden().getCodigoExterno();
        Orden orden = existeOrden(codigoExterno);

        if (orden.getEstado() != 2)
            throw new UnprocessableException("La orden : "
                    + orden.getCodigoExterno() + " no esta en estado 2, por lo que no puede realizar la carga");

        carga.checkFechasSalidaEsMenorFechaLlegada();

        carga.setOrden(orden);
        if (orden.getCamion().getPreset() <= carga.getMasaAcumuladaKg()) { //si el tanque esta lleno
            cargaDAO.save(carga);
            orden.setEstado(3);
            ordenNegocio.modificar(orden);  //actualizo la orden
            throw new UnprocessableException("Tanque lleno");
        }

        //obtengo y guardo los promedios de las cargas,
        // lo tengo que poner porque sino hay cargas cargadas entonces no hay promedio
        try {
            CargaDTO cargaDTOAnterior = getPromedioDensidadAndTemperaturaAndCaudal(codigoExterno);
            orden.setPromedDensidadProductoKilogramoMetroCub(cargaDTOAnterior.getPromedDensidadProductoKilogramoMetroCub());
            orden.setPromedioCaudalLitroSegundo(cargaDTOAnterior.getPromedioCaudalLitroSegundo());
            orden.setPromedioTemperaturaProductoCelcius(cargaDTOAnterior.getPromedioTemperaturaProductoCelcius());
        } catch (NoEncontradoException e) {   //Sino hay carga anterior entonces NoEncontradoException
            log.error(e.getMessage(), e);
            orden.setMasaAcumuladaKg(0);      //digo que la carga inicial de la orden es "cero" si es la primera carga de la orden
        }

        if(carga.getTemperaturaProductoCelcius() > orden.getUmbralTemperaturaCombustible())
            generarEvento(carga, CargaEvent.Tipo.SUPERADO_UMBRAL_DE_TEMPERATURA,orden);


        //controlo que la carga acumulada actual sea mayor que la anterior, tirar excepcion
        if (!isValidoMasaAcumuadaActualCargaConLaAnterior(orden, carga))
            throw new UnprocessableException("La masa Acumulada actual debe ser mayor  a la masa acumulada de la carga anterior");
        orden.setMasaAcumuladaKg(carga.getMasaAcumuladaKg());
        orden.setUltimoCaudalLitroSegundo(carga.getCaudalLitroSegundo());
        orden.setUltimaDensidadProductoKilogramoMetroCub(carga.getDensidadProductoKilogramoMetroCub());
        orden.setUltimaTemperaturaProductoCelcius(carga.getTemperaturaProductoCelcius());
        orden.setFechaFinProcesoCarga(new Date());

        if (orden.getFechaInicioProcesoCarga() == null)
            orden.setFechaInicioProcesoCarga(new Date());

        ordenNegocio.modificar(orden);
        Carga cargaNueva = carga;
        //sino hay cargas en la bd entonces la tiempo inicial es
        if(listado().size()==0 || proximoTiempoLimite == null) { //en caso de que se corte la luz
            cargaNueva = cargaDAO.save(carga);
            proximoTiempoLimite = sumarFrecuenciaConTiempo(orden.getFrecuencia(), orden.getFechaInicioProcesoCarga());
        }
        if(proximoTiempoLimite.compareTo(carga.getFechaEntradaBackEnd())<0) {
            cargaNueva = cargaDAO.save(carga);
            proximoTiempoLimite = sumarFrecuenciaConTiempo(orden.getFrecuencia(),proximoTiempoLimite);
        }
        m.setCodigo(0);
        m.setMensaje(cargaNueva.toString());
        return r;
    }


    private void  generarEvento(Carga carga, CargaEvent.Tipo tipo,Orden orden) throws ConflictException, NoEncontradoException, NegocioException, EncontradoException, BadRequest {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario user = (Usuario) auth.getPrincipal();
        Alarma a = new Alarma();
        a.setOrden(orden);
        a.setAutor(user);
        a.setDescripcion("Humbral de temperatura superado de la orden (codigo externo) " + orden.getCodigoExterno() + " con una temperatura de " + carga.getTemperaturaProductoCelcius());
        alarmaNegocio.agregar(a);
        //orden.setAlarmaActiva(true);
        ordenNegocio.modificar(orden);
        appEventPublisher.publishEvent(new CargaEvent(carga,tipo));
    }

    private Date sumarFrecuenciaConTiempo(int frecuenciaEnSegundos, Date proximoTiempoLimite){
        Calendar cal = Calendar.getInstance();
        cal.setTime(proximoTiempoLimite);

        //Le cambiamos los segundos
        cal.set(Calendar.SECOND,cal.get(Calendar.SECOND)+frecuenciaEnSegundos);
        return cal.getTime();
    }

    private boolean isValidoMasaAcumuadaActualCargaConLaAnterior(Orden orden, Carga carga) {
        if (orden.getMasaAcumuladaKg() < carga.getMasaAcumuladaKg())
            return true;
        return false;
    }

    private void validarMetadata(Carga carga) throws BadRequest {
        if (carga.checkBasicData() != null)
            throw new BadRequest(carga.checkBasicData());
    }

    private Orden existeOrden(String codigoExterno) throws NoEncontradoException {
        Orden orden = ordenNegocio.findByCodigoExterno(codigoExterno);
        if (null == orden)
            throw new NoEncontradoException("No existe una orden con codigo externo: "
                    + codigoExterno);
        return orden;
    }

    @Override
    public CargaDTO getPromedioDensidadAndTemperaturaAndCaudal(String codigoExterno) throws NegocioException, NoEncontradoException {
        Orden orden = existeOrden(codigoExterno);
        if (orden.getCargaList().size() == 0)
            throw new NoEncontradoException("No hay cargas registradas en esta orden, por lo que el promedio sera calculado ");

        try {
            //calculo los datos de la orden y luego los actualizo
            return cargaDAO.getPromedioDensidadAndTemperaturaAndCaudal(orden.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override
    public Carga modificar(Carga carga) throws NegocioException, NoEncontradoException {
        cargar(carga.getId());
        return saveCarga(carga);
    }


    private Carga saveCarga(Carga carga) throws NegocioException {
        try {
            return cargaDAO.save(carga); // sino existe la carga lo cargo
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override
    public void eliminar(long id) throws NegocioException, NoEncontradoException {
        cargar(id);
        try {
            cargaDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }
}
