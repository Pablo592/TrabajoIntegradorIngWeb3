package ar.edu.iua.iw3.web;

import ar.edu.iua.iw3.modelo.Producto;
import ar.edu.iua.iw3.negocio.IProductoNegocio;
import ar.edu.iua.iw3.negocio.ProductoNegocio;
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
public class ProductoRestController {

    @Autowired
    private IProductoNegocio productoNegocio;

    private Logger log = LoggerFactory.getLogger(ProductoNegocio.class);

    @ApiOperation("Busca todos los productos registrados")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Productos enviados correctamente"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida")
    })
    @GetMapping(value="/productos")
    public ResponseEntity<List<Producto>> listado() {
        try {
            return new ResponseEntity<List<Producto>>(productoNegocio.listado(), HttpStatus.OK);
        } catch (NegocioException e) {
            return new ResponseEntity<List<Producto>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation("Registrar un nuevo producto")
    @ApiResponses( value = {
            @ApiResponse(code = 201 , message = "Producto registrado correctamente"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida"),
            @ApiResponse(code = 302 , message = "El producto ya se encuentra registrado")
    })
    @PostMapping(value="/productos")
    public ResponseEntity<String> agregar(@RequestBody Producto producto) {
        try {
            Producto respuesta=productoNegocio.agregar(producto);
            HttpHeaders responseHeaders=new HttpHeaders();
            responseHeaders.set("location", "/producto/"+respuesta.getId());
            return new ResponseEntity<String>(responseHeaders, HttpStatus.CREATED);
        } catch (NegocioException e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EncontradoException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.FOUND);
        }
    }

    @ApiOperation("Modificar un producto")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Producto modificado correctamente"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida"),
            @ApiResponse(code = 404 , message = "No es posible localizar el producto")
    })
    @PutMapping(value="/productos")
    public ResponseEntity<String> modificar(@RequestBody Producto producto) {
        try {
            productoNegocio.modificar(producto);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (NegocioException e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation("Eliminar un producto")
    @ApiResponses( value = {
            @ApiResponse(code = 200 , message = "Producto eliminado correctamente"),
            @ApiResponse(code = 500 , message = "Información incorrecta recibida"),
            @ApiResponse(code = 404 , message = "No es posible localizar el producto")
    })
    @DeleteMapping(value="/productos/{id}")
    public ResponseEntity<String> eliminar(@PathVariable("id") long id) {
        try {
            productoNegocio.eliminar(id);
            return new ResponseEntity<String>(HttpStatus.OK);
        } catch (NegocioException e) {
            return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (NoEncontradoException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
    }
}
