package ar.edu.iua.iw3.web;

import java.util.List;
import ar.edu.iua.iw3.modelo.Camion;
import ar.edu.iua.iw3.negocio.excepciones.BadRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ar.edu.iua.iw3.negocio.CamionNegocio;
import ar.edu.iua.iw3.negocio.ICamionNegocio;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;

@RestController
@RequestMapping(Constantes.URL_BASE)
public class CamionRestController {

	
	@Autowired
	private ICamionNegocio camionNegocio;
	
	private Logger log = LoggerFactory.getLogger(CamionNegocio.class);

	@ApiOperation("Busca todos los camiones registrados")
	@ApiResponses( value = {
			@ApiResponse(code = 200 , message = "Camiones enviados correctamente"),
			@ApiResponse(code = 500 , message = "Información incorrecta recibida")
	})
	@GetMapping(value="/camiones")
	public ResponseEntity<List<Camion>> listado() {
		try {
			return new ResponseEntity<List<Camion>>(camionNegocio.listado(), HttpStatus.OK);
		} catch (NegocioException e) {
			return new ResponseEntity<List<Camion>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@ApiOperation("Registrar un nuevo camión")
	@ApiResponses( value = {
			@ApiResponse(code = 201 , message = "Camión registrado correctamente"),
			@ApiResponse(code = 302 , message = "El camión ya se encuentra registrado"),
			@ApiResponse(code = 500 , message = "Información incorrecta recibida")
	})
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
		} catch (BadRequest e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
	}

	@ApiOperation("Modificar un camión registrado")
	@ApiResponses( value = {
			@ApiResponse(code = 200 , message = "Camión modificado correctamente"),
			@ApiResponse(code = 404 , message = "No es posible localizar el camión"),
			@ApiResponse(code = 500 , message = "Información incorrecta recibida")
	})
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

	@ApiOperation("Eliminar un camión")
	@ApiResponses( value = {
			@ApiResponse(code = 200 , message = "Camión eliminado correctamente"),
			@ApiResponse(code = 404 , message = "No es posible localizar el camión"),
			@ApiResponse(code = 500 , message = "Información incorrecta recibida")
	})
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
