package ar.edu.iua.iw3.web;

import ar.edu.iua.iw3.modelo.Carga;
import ar.edu.iua.iw3.negocio.CargaNegocio;
import ar.edu.iua.iw3.negocio.ICargaNegocio;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CargaRestController {

    @Autowired
    private ICargaNegocio cargaNegocio;

    private Logger log = LoggerFactory.getLogger(CargaNegocio.class);

    @GetMapping(value="/carga")
    public ResponseEntity<List<Carga>> listado() {
        try {
            return new ResponseEntity<List<Carga>>(cargaNegocio.listado(), HttpStatus.OK);
        } catch (NegocioException e) {
            return new ResponseEntity<List<Carga>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value="/carga")
    public ResponseEntity<String> agregar(@RequestBody Carga carga) {
        try {
            Carga respuesta=cargaNegocio.agregar(carga);
            HttpHeaders responseHeaders=new HttpHeaders();
            responseHeaders.set("location", "/carga/"+respuesta.getId());
            return new ResponseEntity<String>(responseHeaders, HttpStatus.CREATED);
        } catch (NegocioException e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.FOUND);
        }
    }

    @PutMapping(value="/carga")
    public ResponseEntity<String> modificar(@RequestBody Carga carga) {
        try {
            cargaNegocio.modificar(carga);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value="/carga/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") long id) {
        try {
            cargaNegocio.eliminar(id);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (NegocioException e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }
}
