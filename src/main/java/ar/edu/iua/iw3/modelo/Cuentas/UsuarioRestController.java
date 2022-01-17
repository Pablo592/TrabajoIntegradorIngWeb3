package ar.edu.iua.iw3.modelo.Cuentas;

import ar.edu.iua.iw3.modelo.Producto;
import ar.edu.iua.iw3.negocio.excepciones.BadRequest;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
import ar.edu.iua.iw3.web.Constantes;
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
            return new ResponseEntity<Usuario>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
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
    public ResponseEntity<String> agregar(@RequestBody Usuario usuario) {
        try {
            Usuario respuesta=usuarioNegocio.agregar(usuario);
            HttpHeaders responseHeaders=new HttpHeaders();
            responseHeaders.set("location", "/usuarios/"+respuesta.getId());
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

    @ApiOperation("Modificar un usuario registrado")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Usuario modificado correctamente"),
            @ApiResponse(code = 404 , message = "No es posible localizar el usuario"),
            @ApiResponse(code = 500 , message = "Error del servidor")
    })

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value="/usuarios")
    public ResponseEntity<String> modificar(@RequestBody Usuario usuario) {
        try {
            usuarioNegocio.modificar(usuario);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        } catch (EncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.FOUND);
        } catch (BadRequest e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<String> eliminar(@PathVariable("id") int id) {
        try {
            usuarioNegocio.eliminar(id);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (NegocioException e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }
}
