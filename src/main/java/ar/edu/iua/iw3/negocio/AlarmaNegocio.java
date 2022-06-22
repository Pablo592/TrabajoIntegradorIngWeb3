package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Alarma;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.modelo.Cuentas.UsuarioNegocio;
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
    private UsuarioNegocio usuarioNegocio;

    @Autowired
    private OrdenNegocio ordenNegocio;

    @Override
    public List<Alarma> listado() throws NegocioException {
        try {
            return alarmaDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

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
    //recibe los datos de una alarma con 2 datos mas, el id del autor y el codigo externo de la orden de la alarma, junto con los datos de la alarma
    public RespuestaGenerica<Alarma> agregar(Alarma alarma) throws NegocioException, EncontradoException, BadRequest, NoEncontradoException {
        MensajeRespuesta m = new MensajeRespuesta();
        RespuestaGenerica<Alarma> r = new RespuestaGenerica<Alarma>(alarma, m);

        alarma.setFecha_HR_MM_registrada(new Date());
        Alarma alarmaNueva =  saveAlarma(alarma);

        m.setCodigo(0);//cero esta todo ok
        m.setMensaje(alarmaNueva.toString());
        return r;
    }

    @Override
    public RespuestaGenerica<Alarma> modificar(Alarma alarma) throws NegocioException, NoEncontradoException {
        cargar(alarma.getId());

        MensajeRespuesta m=new MensajeRespuesta();
        RespuestaGenerica<Alarma> r = new RespuestaGenerica<Alarma>(alarma, m);
        Alarma alarmaNueva = saveAlarma(alarma);
        m.setCodigo(0);
        m.setMensaje(alarmaNueva.toString());
        return r;
    }

    @Override   //solo se modifica la fecha de aceptacion y el usuario que acepta la alarma
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
            return alarmaDAO.save(alarma); // sino existe la alarma la cargo
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override
    public void eliminar(long id) throws NegocioException, NoEncontradoException {
        Alarma alarma = cargar(id);
        try {
            //primero tengo que eliminar la asocioacion entre el autor y la alarma
            Usuario u = usuarioNegocio.findByid(alarma.getAutor().getId());
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
