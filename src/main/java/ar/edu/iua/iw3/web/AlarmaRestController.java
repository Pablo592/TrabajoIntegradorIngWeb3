package ar.edu.iua.iw3.web;

import ar.edu.iua.iw3.modelo.Alarma;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.negocio.AlarmaNegocio;
import ar.edu.iua.iw3.negocio.IAlarmaNegocio;
import ar.edu.iua.iw3.negocio.excepciones.*;
import ar.edu.iua.iw3.util.Constantes;
import ar.edu.iua.iw3.util.MensajeRespuesta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(Constantes.URL_ALARMAS)
public class AlarmaRestController {

    @Autowired
    private IAlarmaNegocio alarmaNegocio;

    private Logger log = LoggerFactory.getLogger(AlarmaNegocio.class);

    @GetMapping(value="/listar")
    public ResponseEntity<List<Alarma>> listado() {
        try {
            return new ResponseEntity<List<Alarma>>(alarmaNegocio.listado(), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<List<Alarma>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value="/listar-author/{id}")
    public ResponseEntity<List<Alarma>> listadoAuthor(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<List<Alarma>>(alarmaNegocio.listarPorAutor(id), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<List<Alarma>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<List<Alarma>>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value="")
    public ResponseEntity<MensajeRespuesta>  agregar(@RequestBody Alarma alarma) {
        try {
            MensajeRespuesta r=alarmaNegocio.agregar(alarma).getMensaje();
            return new ResponseEntity<MensajeRespuesta>(r, HttpStatus.CREATED);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EncontradoException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.FOUND);
        } catch (BadRequest e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.BAD_REQUEST);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value="")
    public ResponseEntity<MensajeRespuesta> modificar(@RequestBody Alarma alarma) {
        try {
            MensajeRespuesta r = alarmaNegocio.modificar(alarma).getMensaje();
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            log.error(e.getMessage(), e);
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value="/aceptarAlarma")
    public ResponseEntity<MensajeRespuesta> aceptarAlarma(@RequestBody Alarma alarma) {
        try {
            alarma.setFechaAceptacion(new Date());
            MensajeRespuesta r = alarmaNegocio.aceptarAlarma(alarma).getMensaje();
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.NOT_FOUND);
        } catch (ConflictException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping(value="/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") long id) {
        try {
            alarmaNegocio.eliminar(id);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }
}
