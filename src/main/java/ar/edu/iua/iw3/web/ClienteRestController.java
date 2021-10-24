package ar.edu.iua.iw3.web;

import java.util.List;

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

import ar.edu.iua.iw3.modelo.Cliente;
import ar.edu.iua.iw3.negocio.ClienteNegocio;
import ar.edu.iua.iw3.negocio.IClienteNegocio;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;

@RestController
public class ClienteRestController {

	@Autowired
	private IClienteNegocio clienteNegocio;
	
	
	private Logger log = LoggerFactory.getLogger(ClienteNegocio.class);

	@GetMapping(value="/clientes")
	public ResponseEntity<List<Cliente>> listado() {
		try {
			return new ResponseEntity<List<Cliente>>(clienteNegocio.listado(), HttpStatus.OK);
		} catch (NegocioException e) {
			return new ResponseEntity<List<Cliente>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	@PostMapping(value="/clientes")
	public ResponseEntity<String> agregar(@RequestBody Cliente cliente) {
		try {
			Cliente respuesta=clienteNegocio.agregar(cliente);
			HttpHeaders responseHeaders=new HttpHeaders();
			responseHeaders.set("location", "/clientes/"+respuesta.getId());
			return new ResponseEntity<String>(responseHeaders, HttpStatus.CREATED);
		} catch (NegocioException e) {
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (EncontradoException e) {
			log.error(e.getMessage(), e);	
			return new ResponseEntity<String>(HttpStatus.FOUND);
		}
	}
	
	
	@PutMapping(value="/clientes")
	public ResponseEntity<String> modificar(@RequestBody Cliente cliente) {
		try {
			clienteNegocio.modificar(cliente);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (NegocioException e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NoEncontradoException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
	}
	
	@DeleteMapping(value="/clientes/{id}")
	public ResponseEntity<String> eliminar(@PathVariable("id") long id) {
		try {
			clienteNegocio.eliminar(id);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (NegocioException e) {
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NoEncontradoException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
	}
	
}
