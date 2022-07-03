package ar.edu.iua.iw3.web;


import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.modelo.dto.ConciliacionDTO;
import ar.edu.iua.iw3.negocio.IOrdenNegocio;
import ar.edu.iua.iw3.negocio.OrdenNegocio;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping(Constantes.URL_ORDENES)
public class OrdenRestController {
    @Autowired
    private IOrdenNegocio ordenNegocio;

    private Logger log = LoggerFactory.getLogger(OrdenNegocio.class);

    @ApiOperation("Busca todas las ordenes registradas")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Ordenes enviadas correctamente"),
            @ApiResponse(code = 500 , message = "Error interno del servidor")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value= "",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Orden>> listado() {
        try {
            return new ResponseEntity<List<Orden>>(ordenNegocio.listado(), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<List<Orden>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation("Devuelve la conciliacion de la orden")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Conciliacion enviada correctamente"),
            @ApiResponse(code = 500 , message = "Error interno del servidor"),
            @ApiResponse(code = 404 , message = "Orden no encontrada"),
            @ApiResponse(code = 422 , message = "Operacion no permitida")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value= "/conciliacion/{codigoExterno}")
    public ResponseEntity<ConciliacionDTO>  getConciliacion(@PathVariable("codigoExterno") String codigoExterno) {
        try {
            return new ResponseEntity<ConciliacionDTO>(ordenNegocio.obtenerConciliacion(codigoExterno), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<ConciliacionDTO>(HttpStatus.INTERNAL_SERVER_ERROR);
        }catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<ConciliacionDTO>(HttpStatus.NOT_FOUND);
        } catch (UnprocessableException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<ConciliacionDTO>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @ApiOperation("Busca una orden en especifico")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Orden enviada correctamente"),
            @ApiResponse(code = 500 , message = "Error interno del servidor"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value= "/buscar/{id}")
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

    @ApiOperation("Datos necesarios para iniciar la etapa 1")
    @ApiResponses( value = {
            @ApiResponse(code = 201 , message = "Orden registrada correctamente"),
            @ApiResponse(code = 302 , message = "La orden ya se encuentra registrada"),
            @ApiResponse(code = 500 , message = "Error interno del servidor"),
            @ApiResponse(code = 400 , message = "Request con informacion inconsistente")
    })
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping(value= "/crear",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MensajeRespuesta> agregarPrimerRequest(@RequestBody Orden orden) {
        try {
            MensajeRespuesta r=ordenNegocio.agregar(orden).getMensaje();
            return new ResponseEntity<MensajeRespuesta>(r, HttpStatus.CREATED);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EncontradoException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.FOUND);
        }catch (BadRequest e){
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation("Datos necesarios para iniciar la etapa 2")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Orden actualizada correctamente"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden"),
            @ApiResponse(code = 500 , message = "Error interno del servidor"),
            @ApiResponse(code = 400 , message = "Request con informacion inconsistente"),
            @ApiResponse(code = 409 , message = "La patente no es vàlida"),
            @ApiResponse(code = 422 , message = "Operacion no permitida")
    })

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value= "/tara")
    public ResponseEntity<MensajeRespuesta> pesoInicialCamion(@RequestBody Orden orden) {
        try {
            MensajeRespuesta r=ordenNegocio.establecerPesajeInicial(orden).getMensaje();
            return new ResponseEntity<MensajeRespuesta>(r, HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.NOT_FOUND);
        } catch (BadRequest e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.BAD_REQUEST);
        } catch (ConflictException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.CONFLICT);
        } catch (UnprocessableException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @ApiOperation("Señal para frenar las cargas y pasar al estado 3")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Orden actualizada correctamente"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden"),
            @ApiResponse(code = 500 , message = "Error interno del servidor"),
            @ApiResponse(code = 409 , message = "La patente no es vàlida")
    })

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value= "/frenar-carga")
    public  ResponseEntity<MensajeRespuesta> frenarCargar(@RequestBody Orden orden) {
        try {
            MensajeRespuesta r=ordenNegocio.frenarCargar(orden.getCodigoExterno()).getMensaje();
            return new ResponseEntity<MensajeRespuesta>(r, HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.NOT_FOUND);
        } catch (UnprocessableException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


    @ApiOperation("Señal para setear el pesaje final y pasar al estado 4")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Orden actualizada correctamente"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden"),
            @ApiResponse(code = 500 , message = "Error interno del servidor"),
            @ApiResponse(code = 409 , message = "La patente no es vàlida")
    })

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value= "/peso-final")
    public ResponseEntity<MensajeRespuesta> pesoFinalCamion(@RequestBody Orden orden) {
        try {
            MensajeRespuesta r=ordenNegocio.establecerPesajeFinal(orden).getMensaje();
            return new ResponseEntity<MensajeRespuesta>(r, HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.NOT_FOUND);
        } catch (UnprocessableException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }


    @ApiOperation("Modificar una orden registrada")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Orden modificada correctamente"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden"),
            @ApiResponse(code = 500 , message = "Error interno del servidor"),
            @ApiResponse(code = 409 , message = "Conflicto con orden ya existente")
    })

    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping(value= "")
    public ResponseEntity<Orden> modificar(@RequestBody Orden orden) {
        try {
           return new ResponseEntity<Orden>( ordenNegocio.modificar(orden),HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.NOT_FOUND);
        } catch (ConflictException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Orden>(HttpStatus.CONFLICT);
        }
    }

    @ApiOperation("Modificar umbral de temperatura del combustible")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Orden modificada correctamente"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden"),
            @ApiResponse(code = 500 , message = "Error interno del servidor"),
            @ApiResponse(code = 400 , message = "Request con informacion inconsistente"),
            @ApiResponse(code = 409 , message = "Conflicto con orden ya existente")
    })

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value= "/umbral-temperatura")
    public ResponseEntity<MensajeRespuesta> modificarUmbralTemperatura(@RequestBody Orden orden) {
        try {
            MensajeRespuesta r = ordenNegocio.cambiarUmbralTemperatura(orden).getMensaje();
            return new ResponseEntity<MensajeRespuesta>( r,HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.NOT_FOUND);
        } catch (BadRequest e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.BAD_REQUEST);
        } catch (ConflictException e) {
            log.error(e.getMessage(), e);
                MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
                return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.CONFLICT);
        }
    }

    @ApiOperation("Eliminar una orden")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Orden eliminada correctamente"),
            @ApiResponse(code = 404 , message = "No es posible localizar la orden"),
            @ApiResponse(code = 500 , message = "Error interno del servidor")
    })

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value= "/{id}")
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
