package ar.edu.iua.iw3.web;

import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.modelo.dto.ConciliacionDTO;
import ar.edu.iua.iw3.negocio.IOrdenNegocio;
import ar.edu.iua.iw3.negocio.OrdenNegocio;
import ar.edu.iua.iw3.negocio.excepciones.*;
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
@RequestMapping(Constantes.URL_BASE)
public class OrdenRestController {
    @Autowired
    private IOrdenNegocio ordenNegocio;

    private Logger log = LoggerFactory.getLogger(OrdenNegocio.class);

    @ApiOperation("Busca todas las ordenes registradas")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Ordenes enviadas correctamente"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida")
    })
    @GetMapping(value= "/ordenes")
    public ResponseEntity<List<Orden>> listado() {
        try {
            return new ResponseEntity<List<Orden>>(ordenNegocio.listado(), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<List<Orden>>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value= "/ordenes/conciliacion/{codigoExterno}")
    public ResponseEntity<ConciliacionDTO> getConciliacion(@PathVariable("codigoExterno") String codigoExterno) {
        try {
            return new ResponseEntity<ConciliacionDTO>(ordenNegocio.obtenerConciliacion(codigoExterno), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<ConciliacionDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<ConciliacionDTO>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Busca una orden en especifico")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Orden enviada correctamente"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden")
    })
    @GetMapping(value= "/ordenes/buscar-una/{id}")    //es el id de orden
    public ResponseEntity<Orden> buscarOrden(@PathVariable("id") long id) {
        try {
            return new ResponseEntity<Orden>(ordenNegocio.cargar(id), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Busca la ultima carga de la orden")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Carga enviada correctamente"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden")
    })
    @GetMapping(value= "/ordenes/ultima-carga/{codigoExterno}")
    public ResponseEntity<Orden> ultimaCarga(@PathVariable("codigoExterno") String codigoExterno) {
        try {
            return new ResponseEntity<Orden>(ordenNegocio.traerUltimaCarga(codigoExterno), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Datos necesarios para iniciar la etapa 1")
    @ApiResponses( value = {
            @ApiResponse(code = 201 , message = "Orden registrada correctamente"),
            @ApiResponse(code = 302 , message = "La orden ya se encuentra registrada"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida")
    })
    @PostMapping(value= "/ordenes/primer-envio")
    public ResponseEntity<String> agregarPrimerRequest(@RequestBody Orden orden) {
        try {
            Orden respuesta=ordenNegocio.agregar(orden);
            HttpHeaders responseHeaders=new HttpHeaders();
            responseHeaders.set("location", "/orden/"+respuesta.getId());
            return new ResponseEntity<String>(responseHeaders, HttpStatus.CREATED);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.FOUND);
        }catch (BadRequest e){
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation("Datos necesarios para iniciar la etapa 2")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Orden actualizada correctamente"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida")
    })
    @PutMapping(value= "/ordenes/tara")
    public ResponseEntity<Orden> pesoInicialCamion(@RequestBody Orden orden) {
        try {
            return new ResponseEntity<Orden>( ordenNegocio.establecerPesajeInicial(orden),HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.NOT_FOUND);
        } catch (BadRequest e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.BAD_REQUEST);
        } catch (ConflictException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.CONFLICT);
        }
    }

    @ApiOperation("Señal para frenar las cargas y pasar al estado 3")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Orden actualizada correctamente"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida")
    })
    @PutMapping(value= "/ordenes/frenar-carga")
    public ResponseEntity<Orden> frenarCargar(@RequestBody Orden orden) {
        try {
            return new ResponseEntity<Orden>(ordenNegocio.frenarCargar(orden.getCodigoExterno()), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Señal para setear el pesaje final y pasar al estado 4")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Orden actualizada correctamente"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida")
    })
    @PutMapping(value= "/ordenes/peso-final")
    public ResponseEntity<Orden> pesoFinalCamion(@RequestBody Orden orden) {
        try {
            return new ResponseEntity<Orden>(ordenNegocio.establecerPesajeFinal(orden), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.NOT_FOUND);
        }
    }


    @ApiOperation("Modificar una orden registrada")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Orden modificada correctamente"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida")
    })
    @PutMapping(value= "/ordenes")
    public ResponseEntity<Orden> modificar(@RequestBody Orden orden) {
        try {
           return new ResponseEntity<Orden>( ordenNegocio.modificar(orden),HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            return new ResponseEntity<Orden>(HttpStatus.NOT_FOUND);
        }
    }
    @ApiOperation("Eliminar una orden")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Orden eliminada correctamente"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida")
    })
    @DeleteMapping(value= "/ordenes/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") long id) {
        try {
            ordenNegocio.eliminar(id);
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
