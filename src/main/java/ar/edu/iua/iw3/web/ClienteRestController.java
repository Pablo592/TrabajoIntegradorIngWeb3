package ar.edu.iua.iw3.web;

import java.util.List;
import ar.edu.iua.iw3.util.Constantes;
import ar.edu.iua.iw3.util.MensajeRespuesta;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ar.edu.iua.iw3.modelo.Cliente;
import ar.edu.iua.iw3.negocio.ClienteNegocio;
import ar.edu.iua.iw3.negocio.IClienteNegocio;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;

@RestController
@RequestMapping(Constantes.URL_CLIENTE)
public class ClienteRestController {
    @Autowired
    private IClienteNegocio clienteNegocio;
    private Logger log = LoggerFactory.getLogger(ClienteNegocio.class);

    @ApiOperation("Busca todos los clientes registrados")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Clientes enviados correctamente"),
            @ApiResponse(code = 500, message = "Error interno del servidor")
    })

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "")
    public ResponseEntity<List<Cliente>> listado() {
        try {
            return new ResponseEntity<List<Cliente>>(clienteNegocio.listado(), HttpStatus.OK);
        } catch (NegocioException e) {
            return new ResponseEntity<List<Cliente>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation("Registrar un nuevo chófer")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Cliente registrado correctamente"),
            @ApiResponse(code = 500, message = "Error interno del servidor"),
            @ApiResponse(code = 302, message = "El cliente ya se encuentra registrado")
    })

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value = "")
    public ResponseEntity<MensajeRespuesta> agregar(@RequestBody Cliente cliente) {
        try {
            MensajeRespuesta r=clienteNegocio.agregar(cliente).getMensaje();
            return new ResponseEntity<MensajeRespuesta>(r, HttpStatus.CREATED);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EncontradoException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.FOUND);
        }
    }

    @ApiOperation("Modificar un cliente")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cliente modificado correctamente"),
            @ApiResponse(code = 500, message = "Error interno del servidor"),
            @ApiResponse(code = 404, message = "No es posible localizar al cliente")
    })

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value = "")
    public  ResponseEntity<MensajeRespuesta> modificar(@RequestBody Cliente cliente) {
        try {
            MensajeRespuesta r = clienteNegocio.modificar(cliente).getMensaje();
            return new ResponseEntity<MensajeRespuesta>(HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Eliminar un cliente")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cliente eliminado correctamente"),
            @ApiResponse(code = 500, message = "Error interno del servidor"),
            @ApiResponse(code = 404, message = "No es posible localizar al cliente")
    })

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
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
