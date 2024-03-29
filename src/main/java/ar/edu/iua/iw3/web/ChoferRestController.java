package ar.edu.iua.iw3.web;

import java.util.List;
import ar.edu.iua.iw3.util.Constantes;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ar.edu.iua.iw3.modelo.Chofer;
import ar.edu.iua.iw3.negocio.ChoferNegocio;
import ar.edu.iua.iw3.negocio.IChoferNegocio;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;

@RestController
@RequestMapping(Constantes.URL_CHOFER)
public class ChoferRestController {

	@Autowired
	private IChoferNegocio choferNegocio;
	
	private Logger log = LoggerFactory.getLogger(ChoferNegocio.class);

	@ApiOperation("Busca todos los choferes registrados")
	@ApiResponses( value = {
			@ApiResponse(code = 200 , message = "Choferes enviados correctamente"),
			@ApiResponse(code = 500 , message = "Error interno del servidor")
	})
	@GetMapping(value="")
	public ResponseEntity<List<Chofer>> listado() {
		try {
			return new ResponseEntity<List<Chofer>>(choferNegocio.listado(), HttpStatus.OK);
		} catch (NegocioException e) {
			return new ResponseEntity<List<Chofer>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation("Registrar un nuevo chófer")
	@ApiResponses( value = {
			@ApiResponse(code = 201 , message = "Chófer registrado correctamente"),
			@ApiResponse(code = 500 , message = "Error interno del servidor"),
			@ApiResponse(code = 302 , message = "El chófer ya se encuentra registrado")
	})
	@PostMapping(value="")
	public ResponseEntity<String> agregar(@RequestBody Chofer chofer) {
		try {
			Chofer respuesta=choferNegocio.agregar(chofer);
			HttpHeaders responseHeaders=new HttpHeaders();
			responseHeaders.set("location", "/choferes/"+respuesta.getId());
			return new ResponseEntity<String>(responseHeaders, HttpStatus.CREATED);
		} catch (NegocioException e) {
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (EncontradoException e) {
			log.error(e.getMessage(), e);	
			return new ResponseEntity<String>(HttpStatus.FOUND);
		}
	}


	@ApiOperation("Modificar un chófer")
	@ApiResponses( value = {
			@ApiResponse(code = 200 , message = "Chófer modificado correctamente"),
			@ApiResponse(code = 500 , message = "Error interno del servidor"),
			@ApiResponse(code = 404 , message = "No es posible localizar al chófer")
	})

	@PreAuthorize("hasRole('ROLE_USER')")
	@PutMapping(value="")
	public ResponseEntity<String> modificar(@RequestBody Chofer chofer) {
		try {
			choferNegocio.modificar(chofer);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (NegocioException e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NoEncontradoException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
	}

	@ApiOperation("Eliminar un chófer")
	@ApiResponses( value = {
			@ApiResponse(code = 200 , message = "Chófer eliminado correctamente"),
			@ApiResponse(code = 500 , message = "Error interno del servidor"),
			@ApiResponse(code = 404 , message = "No es posible localizar al chófer")
	})

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(value="/{id}")
	public ResponseEntity<String> eliminar(@PathVariable("id") long id) {
		try {
			choferNegocio.eliminar(id);
			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (NegocioException e) {
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NoEncontradoException e) {
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		}
	}
}
