package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Alarma;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.modelo.Cuentas.UsuarioNegocio;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.modelo.persistencia.AlarmaRepository;
import ar.edu.iua.iw3.negocio.excepciones.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AlarmaNegocio implements IAlarmaNegocio{

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
            o = alarmaDAO.findAllByAutor_Id((int)id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
        if (!o.isPresent()) {
            throw new NoEncontradoException("No hay alarmas pertenecientes a este autor" + id);
        }
        for(Alarma a :o.get()){
           if(a.getOrden().isEnviarMailActivo() && (a.getFechaAceptacion() == null))
               alarmaActiva.add(a);
        }

        return alarmaActiva;
    }


    @Override   //recibe los datos de una alarma con 2 datos mas, el id del autor y el codigo externo de la orden de la alarma, junto con los datos de la alarma
    public Alarma agregar(Alarma alarma) throws NegocioException, EncontradoException, BadRequest,NoEncontradoException {
            Usuario autor = usuarioNegocio.findByid(alarma.getAutor().getId());
            Orden orden = ordenNegocio.findByCodigoExterno(alarma.getOrden().getCodigoExterno());
            if(autor == null || orden == null )
                throw new NoEncontradoException("El autor o la orden asociada a la alarma no existe");

            //como los datos de usuario y de orden no viene completos los obtengo y se los coloco a la alarma
            alarma.setAutor(autor);
            alarma.setOrden(orden);
            alarma.setFechaAceptacion(new Date());      //tengo que hacer un end-point diferente
            //actualizo la fecha de aceptacion
            return saveAlarma(alarma);
    }

    @Override
    public Alarma modificar(Alarma alarma) throws NegocioException, NoEncontradoException, ConflictException {
        Alarma alarmaBD = cargar(alarma.getId());
        if(alarma.getAutor() == null)
            alarma.setAutor(usuarioNegocio.findByid(alarmaBD.getAutor().getId()));
        if(alarma.getOrden() == null)
            alarma.setOrden(ordenNegocio.findByCodigoExterno(alarmaBD.getOrden().getCodigoExterno()));
        if(alarma.getUsuarioAceptador() == null)
            alarma.setUsuarioAceptador(alarmaBD.getUsuarioAceptador());
        if(alarma.getFechaAceptacion() == null)
            alarma.setFechaAceptacion(alarmaBD.getFechaAceptacion());
        if(alarmaBD.getFechaAceptacion() != alarma.getFechaAceptacion())
            throw new ConflictException("No se puede cambiar la fecha de aceptacion de la alarma");

        return saveAlarma(alarma);
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
            for(int a = 0; a<u.getAlarmaList().size(); a++){
                if(u.getAlarmaList().get(a).getId()  == id)
                    u.getAlarmaList().remove(a);
            }
            usuarioNegocio.modificar(u);
            alarmaDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override   //solo se modifica la fecha de aceptacion y el usuario que acepta la alarma
    public Alarma aceptarAlarma(Alarma alarma) throws NegocioException, NoEncontradoException, ConflictException {
        Alarma alarmaBD = cargar(alarma.getId());
        if(alarmaBD.getFechaAceptacion() != null)
            throw new ConflictException("La alarma con descripcion :" + alarma.getDescripcion() + " ya fue aceptada, no se pueden aceptar alarmas por segunda vez");

        Usuario usuarioAceptador = usuarioNegocio.cargarPorUsernameOEmail(alarma.getUsuarioAceptador().getUsername());

        alarma.setAutor( usuarioNegocio.cargar(alarmaBD.getAutor().getId()));
        alarma.setOrden(ordenNegocio.findByCodigoExterno(alarmaBD.getOrden().getCodigoExterno()));
        alarma.setUsuarioAceptador(usuarioAceptador);
        return saveAlarma(alarma);
    }
}
