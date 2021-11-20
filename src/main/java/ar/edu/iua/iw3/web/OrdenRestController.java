package ar.edu.iua.iw3.web;

import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.negocio.IOrdenNegocio;
import ar.edu.iua.iw3.negocio.OrdenNegocio;
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
public class OrdenRestController {
    @Autowired
    private IOrdenNegocio ordenNegocio;

    private Logger log = LoggerFactory.getLogger(OrdenNegocio.class);

    @GetMapping(value= "/ordenes")
    public ResponseEntity<List<Orden>> listado() {
        try {
            return new ResponseEntity<List<Orden>>(ordenNegocio.listado(), HttpStatus.OK);
        } catch (NegocioException e) {
            return new ResponseEntity<List<Orden>>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value= "/ordenes/resumen/{numeroOrden}")
    public ResponseEntity<Orden> resumen(@PathVariable("numeroOrden") long numeroOrden) {
        try {
            return new ResponseEntity<Orden>(ordenNegocio.cargar(numeroOrden), HttpStatus.OK);
        } catch (NegocioException e) {
            return new ResponseEntity<Orden>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            return new ResponseEntity<Orden>(HttpStatus.NOT_FOUND);
        }
    }



    @PostMapping(value= "/ordenes")
    public ResponseEntity<String> agregar(@RequestBody Orden orden) {
        try {
            Orden respuesta=ordenNegocio.agregar(orden);
            HttpHeaders responseHeaders=new HttpHeaders();
            responseHeaders.set("location", "/orden/"+respuesta.getId());
            return new ResponseEntity<String>(responseHeaders, HttpStatus.CREATED);
        } catch (NegocioException e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.FOUND);
        }
    }

    @PostMapping(value= "/ordenes-primerEnvio")
    public ResponseEntity<String> agregarPrimerRequest(@RequestBody Orden orden) {
        try {
            Orden respuesta=ordenNegocio.agregar(orden);
            HttpHeaders responseHeaders=new HttpHeaders();
            responseHeaders.set("location", "/orden/"+respuesta.getId());
            return new ResponseEntity<String>(responseHeaders, HttpStatus.CREATED);
        } catch (NegocioException e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.FOUND);
        }
    }

    @PutMapping(value= "/ordenes")
    public ResponseEntity<String> modificar(@RequestBody Orden orden) {
        try {
            ordenNegocio.modificar(orden);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value= "/ordenes/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") long id) {
        try {
            ordenNegocio.eliminar(id);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (NegocioException e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

}
