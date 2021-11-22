package ar.edu.iua.iw3.web;

import ar.edu.iua.iw3.modelo.Carga;
import ar.edu.iua.iw3.modelo.dto.CargaDTO;
import ar.edu.iua.iw3.negocio.CargaNegocio;
import ar.edu.iua.iw3.negocio.ICargaNegocio;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
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

import java.util.List;

@RestController
public class CargaRestController {

    @Autowired
    private ICargaNegocio cargaNegocio;

    private Logger log = LoggerFactory.getLogger(CargaNegocio.class);

    @ApiOperation("Busca todos las cargas registradas")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Cargas enviadas correctamente"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida")
    })
    @GetMapping(value= "/cargas")
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
    @GetMapping(value= "/promedio-cargas-por-codigo-externo")
    public ResponseEntity<CargaDTO> ResumenCargaPorId(@RequestParam("codigoExterno") String codigoExterno) {
        try {
            return new ResponseEntity<CargaDTO>(cargaNegocio.getAcumulacionAndPromedioCargas(codigoExterno), HttpStatus.OK);
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
            @ApiResponse(code = 500 , message = "Información incorrecta recibida"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden")
    })
    @PostMapping(value= "/cargas")
    public ResponseEntity<String> agregar(@RequestBody Carga carga) {
        try {
            Carga respuesta=cargaNegocio.agregar(carga);
            HttpHeaders responseHeaders=new HttpHeaders();
            responseHeaders.set("location", "/carga/"+respuesta.getId());
            return new ResponseEntity<String>(responseHeaders, HttpStatus.CREATED);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Modificar una carga registrada")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Carga modificada correctamente"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida"),
            @ApiResponse(code = 404 , message = "No es posible localizar la carga")
    })
    @PutMapping(value= "/cargas")
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
            @ApiResponse(code = 500 , message = "Información incorrecta recibida"),
            @ApiResponse(code = 404 , message = "No es posible localizar la carga")
    })
    @DeleteMapping(value= "/cargas/{id}")
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
