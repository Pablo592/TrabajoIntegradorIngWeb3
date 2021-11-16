package ar.edu.iua.iw3.web;

import java.util.List;

import ar.edu.iua.iw3.modelo.Camion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.iua.iw3.negocio.CamionNegocio;
import ar.edu.iua.iw3.negocio.ICamionNegocio;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;

@RestController
public class CamionRestController {

	
	@Autowired
	private ICamionNegocio camionNegocio;
	
	private Logger log = LoggerFactory.getLogger(CamionNegocio.class);

	@GetMapping(value="/camiones")
	public ResponseEntity<List<Camion>> listado() {
		try {
			return new ResponseEntity<List<Camion>>(camionNegocio.listado(), HttpStatus.OK);
		} catch (NegocioException e) {
			return new ResponseEntity<List<Camion>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping(value="/camiones")
	public ResponseEntity<String> agregar(@RequestBody Camion camion) {
		try {
			Camion respuesta=camionNegocio.agregar(camion);
			HttpHeaders responseHeaders=new HttpHeaders();
			responseHeaders.set("location", "/camiones/"+respuesta.getId());
			return new ResponseEntity<String>(responseHeaders, HttpStatus.CREATED);
		} catch (NegocioException e) {
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (EncontradoException e) {
			log.error(e.getMessage(), e);	
			return new ResponseEntity<String>(HttpStatus.FOUND);
		}
	}
	
	
	@PutMapping(value="/camiones")
	public ResponseEntity<String> modificar(@RequestBody Camion camion) {
		try {
			camionNegocio.modificar(camion);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (NegocioException e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NoEncontradoException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping(value="/camiones/{id}")
	public ResponseEntity<String> eliminar(@PathVariable("id") long id) {
		try {
			camionNegocio.eliminar(id);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (NegocioException e) {
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NoEncontradoException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
	}
	
}
