package ar.edu.iua.iw3.web;

import ar.edu.iua.iw3.modelo.Carga;
import ar.edu.iua.iw3.modelo.dto.CargaDTO;
import ar.edu.iua.iw3.negocio.CargaNegocio;
import ar.edu.iua.iw3.negocio.ICargaNegocio;
import ar.edu.iua.iw3.negocio.excepciones.*;
import ar.edu.iua.iw3.util.Constantes;
import ar.edu.iua.iw3.util.MensajeRespuesta;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(Constantes.URL_CARGAS)
public class CargaRestController {

    @Autowired
    private ICargaNegocio cargaNegocio;

    private Logger log = LoggerFactory.getLogger(CargaNegocio.class);

    @ApiOperation("Busca todos las cargas registradas")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Cargas enviadas correctamente"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida")
    })

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value= "")
    public ResponseEntity<List<Carga>> listado() {
        try {
            return new ResponseEntity<List<Carga>>(cargaNegocio.listado(), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<List<Carga>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation("Busca los valores promedio de las cargas pertenecientes a una orden en especifico")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Promedios de cargas enviados correctamente"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida"),
            @ApiResponse(code = 404 , message = "No es posible localizar la carga")
    })

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value= "/promedio")
    public ResponseEntity<CargaDTO> CargaPromedioCodigoExterno(@RequestParam("codigoExterno") String codigoExterno) {
        try {
            return new ResponseEntity<CargaDTO>(cargaNegocio.getPromedioDensidadAndTemperaturaAndCaudal(codigoExterno), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<CargaDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
        return new ResponseEntity<CargaDTO>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Registrar una nueva carga")
    @ApiResponses( value = {
            @ApiResponse(code = 201 , message = "Carga registrada correctamente"),
            @ApiResponse(code = 500 , message = "Error interno del servidor"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden"),
            @ApiResponse(code = 400 , message = "Request con informacion inconsistente"),
            @ApiResponse(code = 422 , message = "Operacion no permitida"),
            @ApiResponse(code = 409 , message = "Inconsistencia temporal de sucesos")
    })

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value= "",produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MensajeRespuesta> agregar(@RequestBody Carga carga) {
        try {
            carga.setFechaEntradaBackEnd(new Date());
            MensajeRespuesta r=cargaNegocio.agregar(carga).getMensaje();
            return new ResponseEntity<MensajeRespuesta>(r, HttpStatus.CREATED);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.NOT_FOUND);
        } catch (BadRequest e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.BAD_REQUEST);
        } catch (UnprocessableException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (ConflictException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.CONFLICT);
        }
    }

    @ApiOperation("Modificar una carga registrada")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Carga modificada correctamente"),
            @ApiResponse(code = 500 , message = "Error interno del servidor"),
            @ApiResponse(code = 404 , message = "No es posible localizar la carga")
    })

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value= "")
    public ResponseEntity<String> modificar(@RequestBody Carga carga) {
        try {
            cargaNegocio.modificar(carga);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Eliminar una carga registrada")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Carga eliminada correctamente"),
            @ApiResponse(code = 500 , message = "Error interno del servidor"),
            @ApiResponse(code = 404 , message = "No es posible localizar la carga")
    })

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value= "/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") long id) {
        try {
            cargaNegocio.eliminar(id);
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
