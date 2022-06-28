package ar.edu.iua.iw3.web;

import ar.edu.iua.iw3.modelo.Alarma;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.negocio.AlarmaNegocio;
import ar.edu.iua.iw3.negocio.IAlarmaNegocio;
import ar.edu.iua.iw3.negocio.excepciones.*;
import ar.edu.iua.iw3.util.Constantes;
import ar.edu.iua.iw3.util.MensajeRespuesta;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(Constantes.URL_ALARMAS)
public class AlarmaRestController {

    @Autowired
    private IAlarmaNegocio alarmaNegocio;

    private Logger log = LoggerFactory.getLogger(AlarmaNegocio.class);

    @ApiOperation("Busca todas alarmas generadas durante la utilizacion del sistema por un determinado usuario")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Alarmas listadas correctamente"),
            @ApiResponse(code = 404 , message = "No hay alarmas pertenecientes al autor"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida o error interno del servidor")
    })
    @GetMapping(value="/listar-author/{id}")
    public ResponseEntity<List<Alarma>> listadoAuthor(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<List<Alarma>>(alarmaNegocio.listarPorAutor(id), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<List<Alarma>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<List<Alarma>>(HttpStatus.NOT_FOUND);
        }
    }


    @ApiOperation("Al aceptar la alarma se da constancia que el usuario la conoce")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Alarmas aceptadas correctamente"),
            @ApiResponse(code = 404 , message = "No existe la orden a la que pertenece la alarma"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida o error interno del servidor"),
            @ApiResponse(code = 409 , message = "Se ha producido una incosistencia con los datos ya guardados")
    })
    @PutMapping(value="/aceptarAlarma")
    public ResponseEntity<MensajeRespuesta> aceptarAlarma(@RequestBody Alarma alarma) {
        try {
            alarma.setFechaAceptacion(new Date());
            MensajeRespuesta r = alarmaNegocio.aceptarAlarma(alarma).getMensaje();
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.NOT_FOUND);
        } catch (ConflictException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.CONFLICT);
        }
    }


    @ApiOperation("Eliminar alarma por id")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Alarma eliminada correctamente"),
            @ApiResponse(code = 404 , message = "No existe la alarma o el autor a la que pertenece"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida o error interno del servidor"),
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value="/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") long id) {
        try {
            alarmaNegocio.eliminar(id);
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
