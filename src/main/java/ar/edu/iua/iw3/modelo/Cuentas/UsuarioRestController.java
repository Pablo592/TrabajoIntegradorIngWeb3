package ar.edu.iua.iw3.modelo.Cuentas;

import ar.edu.iua.iw3.negocio.excepciones.*;
import ar.edu.iua.iw3.util.MensajeRespuesta;
import ar.edu.iua.iw3.util.Constantes;
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
import java.util.List;

@RestController
@RequestMapping(Constantes.URL_BASE)
public class UsuarioRestController {

    @Autowired
    private IUsuarioNegocio usuarioNegocio;

    private Logger log = LoggerFactory.getLogger(UsuarioNegocio.class);


    @ApiOperation("Busca todos los usuarios registrados")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Usuarios enviados correctamente"),
            @ApiResponse(code = 500 , message = "Error del servidor")
    })

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value="/usuarios")
    public ResponseEntity<List<Usuario>> listado() {
        try {
            return new ResponseEntity<List<Usuario>>(usuarioNegocio.lista(), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<List<Usuario>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @ApiOperation("Busca un usuario registrado")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Usuario enviado correctamente"),
            @ApiResponse(code = 500 , message = "Error del servidor")
    })

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value="/usuarios/{id}")
    public ResponseEntity<Usuario> cargar(@PathVariable("id") int id) {
        try {
            return new ResponseEntity<Usuario>(usuarioNegocio.cargar(id), HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Usuario>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<Usuario>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Registrar un nuevo usuario")
    @ApiResponses( value = {
            @ApiResponse(code = 201 , message = "Usuario registrado correctamente"),
            @ApiResponse(code = 302 , message = "El usuario ya se encuentra registrado"),
            @ApiResponse(code = 500 , message = "Error del servidor")
    })

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value="/usuarios")
    public ResponseEntity<MensajeRespuesta> agregar(@RequestBody Usuario usuario) {
        try {
            MensajeRespuesta r = usuarioNegocio.agregar(usuario).getMensaje();
            return new ResponseEntity<MensajeRespuesta>(r, HttpStatus.CREATED);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EncontradoException e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.FOUND);
        } catch (BadRequest e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation("Modificar un usuario registrado")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Usuario modificado correctamente"),
            @ApiResponse(code = 404 , message = "No es posible localizar el usuario"),
            @ApiResponse(code = 500 , message = "Error del servidor")
    })

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value="/usuarios")
    public ResponseEntity<MensajeRespuesta> modificar(@RequestBody Usuario usuario) {
        try {
            MensajeRespuesta r = usuarioNegocio.modificar(usuario).getMensaje();
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
        } catch (BadRequest e) {
            log.error(e.getMessage(), e);
            MensajeRespuesta r=new MensajeRespuesta(-1,e.getMessage());
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation("Eliminar un usuario")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Usuario eliminado correctamente"),
            @ApiResponse(code = 404 , message = "No es posible localizar el usuario"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida")
    })

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value="/usuarios/{id}")
    public ResponseEntity<MensajeRespuesta> eliminar(@PathVariable("id") int id) {
        try {
            MensajeRespuesta r =   usuarioNegocio.eliminar(id).getMensaje();
            return new ResponseEntity<MensajeRespuesta>(r,HttpStatus.OK);
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
}
