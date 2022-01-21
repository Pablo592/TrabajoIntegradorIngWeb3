package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Alarma;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.modelo.Cuentas.UsuarioNegocio;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.modelo.persistencia.AlarmaRepository;
import ar.edu.iua.iw3.negocio.excepciones.*;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AlarmaNegocio implements IAlarmaNegocio{

    private Logger log = LoggerFactory.getLogger(ClienteNegocio.class);

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

    @Override   //recibe los datos de una alarma con 2 datos mas, el id del autor y el codigo externo de la orden de la alarma, jutnto con los datos de la alarma
    public Alarma agregar(Alarma alarma) throws NegocioException, EncontradoException, BadRequest,NoEncontradoException {
        try{
            Usuario autor = usuarioNegocio.findByid(alarma.getAutor().getId());
            Orden orden = ordenNegocio.findByCodigoExterno(alarma.getOrden().getCodigoExterno());
            if(autor == null || orden == null )
                throw new NoEncontradoException("El autor o la orden asociada a la alarma no existe");
            //configuro los datos de la alarma
            orden.setAlarmaActiva(false);   //cuando creo la alarma no la activo,
                                            // sino que lo hago despues que se envia el mail,
                                            // luego lo chequeo cada vez que se trata de enviar una alarma, si esta aciva no la envio sino lo envio
            //como los datos de usuario y de orden no viene completos los obtengo y se los coloco a la alarma
            alarma.setAutor(autor);
            alarma.setOrden(orden);
            //actualizo la fecha de aceptacion
            alarma.setFechaAceptacion(new Date());
            return alarmaDAO.save(alarma);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override   //le voy a tener que pasar el id de la alarma porque no tengo otra manera de identificarlo
    public Alarma modificar(Alarma alarma) throws NegocioException, NoEncontradoException, ConflictException {
        cargar(alarma.getId());
        Usuario autor = usuarioNegocio.findByid(alarma.getAutor().getId());
        Orden orden = ordenNegocio.findByCodigoExterno(alarma.getOrden().getCodigoExterno());
        if(autor == null || orden == null )
            throw new NoEncontradoException("El autor o la orden asociada a la alarma no existe");
        //configuro los datos de la alarma
        alarma.setAutor(autor);
        alarma.setOrden(orden);
        try {
            return alarmaDAO.save(alarma); //
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }

    }

    @Override
    public void eliminar(long id) throws NegocioException, NoEncontradoException {
        Alarma alarma = cargar(id);
        int posicion = 0;
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



}
