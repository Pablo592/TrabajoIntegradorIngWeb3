package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Alarma;
import ar.edu.iua.iw3.modelo.Cuentas.IUsuarioNegocio;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.modelo.persistencia.AlarmaRepository;
import ar.edu.iua.iw3.negocio.excepciones.*;
import ar.edu.iua.iw3.util.MensajeRespuesta;
import ar.edu.iua.iw3.util.RespuestaGenerica;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AlarmaNegocio implements IAlarmaNegocio {

    private Logger log = LoggerFactory.getLogger(AlarmaNegocio.class);

    @Autowired
    private AlarmaRepository alarmaDAO;

    @Autowired
    private IUsuarioNegocio usuarioNegocio;

    @Autowired
    private IOrdenNegocio ordenNegocio;

    @Override
    public Alarma cargar(long id) throws NegocioException, NoEncontradoException {
        Optional<Alarma> o;
        try {
            o = alarmaDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
        if (!o.isPresent()) {
            throw new NoEncontradoException("No se encuentra la alarma con id=" + id);
        }
        return o.get();
    }

    @Override
    public List<Alarma> listarPorAutor(long id) throws NegocioException, NoEncontradoException {
        Optional<List<Alarma>> o;
        List<Alarma> alarmaActiva = new ArrayList<Alarma>();
        try {
            o = alarmaDAO.findAllByAutor_Id((int) id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
        if (!o.isPresent()) {
            throw new NoEncontradoException("No hay alarmas pertenecientes a este autor" + id);
        }
        for (Alarma a : o.get()) {
            if (a.getOrdenAlarma().isEnviarMailActivo() && (a.getFechaAceptacion() == null))
                alarmaActiva.add(a);
        }

        return alarmaActiva;
    }


    @Override
    public RespuestaGenerica<Alarma> agregar(Alarma alarma) throws NegocioException{
        MensajeRespuesta m = new MensajeRespuesta();
        RespuestaGenerica<Alarma> r = new RespuestaGenerica<Alarma>(alarma, m);
        alarma.setFechaDeCreacion(new Date());
        Alarma alarmaNueva =  saveAlarma(alarma);
        m.setCodigo(0);
        m.setMensaje(alarmaNueva.toString());
        return r;
    }

    @Override
    public RespuestaGenerica<Alarma> aceptarAlarma(Alarma alarma) throws NegocioException, NoEncontradoException, ConflictException {
        MensajeRespuesta m=new MensajeRespuesta();
        RespuestaGenerica<Alarma> r = new RespuestaGenerica<Alarma>(alarma, m);

        Alarma alarmaBD = cargar(alarma.getId());
        if (alarmaBD.getFechaAceptacion() != null)
            throw new ConflictException("La alarma con id :" + alarma.getId() + " ya fue aceptada anteriormente, no se pueden aceptar alarmas por segunda vez");

        Usuario usuarioAceptador = usuarioNegocio.cargarPorUsernameOEmail(alarma.getUsuarioAceptador().getUsername());

        alarmaBD.setUsuarioAceptador(usuarioAceptador);
        alarmaBD.setFechaAceptacion(alarma.getFechaAceptacion());

        Orden orden = alarmaBD.getOrdenAlarma();

        orden.setEnviarMailActivo(false);
        ordenNegocio.modificar(orden);


        Alarma alarmaNueva = saveAlarma(alarmaBD);
        m.setCodigo(0);
        m.setMensaje(alarmaNueva.toString());
        return r;
    }

    private Alarma saveAlarma(Alarma alarma) throws NegocioException {
        try {
            return alarmaDAO.save(alarma);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override
    public void eliminar(long id) throws NegocioException, NoEncontradoException {
        Optional<Alarma> o;
        try {
            o = Optional.ofNullable(cargar(id));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
        if (!o.isPresent()) {
            throw new NoEncontradoException("No existe la alarma con el id " + id);
        }
        try {
            Usuario u = usuarioNegocio.findByid(o.get().getAutor().getId());
            for (int a = 0; a < u.getAlarmaList().size(); a++) {
                if (u.getAlarmaList().get(a).getId() == id)
                    u.getAlarmaList().remove(a);
            }
            usuarioNegocio.modificar(u);
            alarmaDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }


}
