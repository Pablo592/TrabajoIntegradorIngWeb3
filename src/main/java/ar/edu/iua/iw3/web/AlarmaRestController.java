package ar.edu.iua.iw3.web;

import ar.edu.iua.iw3.modelo.Alarma;
import ar.edu.iua.iw3.negocio.AlarmaNegocio;
import ar.edu.iua.iw3.negocio.IAlarmaNegocio;
import ar.edu.iua.iw3.negocio.excepciones.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(Constantes.URL_BASE)
public class AlarmaRestController {

    @Autowired
    private IAlarmaNegocio alarmaNegocio;

    private Logger log = LoggerFactory.getLogger(AlarmaNegocio.class);

    @GetMapping(value="/alarmas/listar")
    public ResponseEntity<List<Alarma>> listado() {
        try {
            return new ResponseEntity<List<Alarma>>(alarmaNegocio.listado(), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<List<Alarma>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value="/alarmas")
    public ResponseEntity<String> agregar(@RequestBody Alarma alarma) {
        try {
            Alarma respuesta=alarmaNegocio.agregar(alarma);
            HttpHeaders responseHeaders=new HttpHeaders();
            responseHeaders.set("location", "/alarma/"+respuesta.getId());
            return new ResponseEntity<String>(responseHeaders, HttpStatus.CREATED);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.FOUND);
        } catch (BadRequest e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping(value="/alarmas")
    public ResponseEntity<String> modificar(@RequestBody Alarma alarma) {
        try {
            alarmaNegocio.modificar(alarma);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        } catch (ConflictException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping(value="/alarmas/{id}")
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
