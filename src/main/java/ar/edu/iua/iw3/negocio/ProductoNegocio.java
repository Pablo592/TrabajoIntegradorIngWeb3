package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Producto;
import ar.edu.iua.iw3.modelo.persistencia.ProductoRepository;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoNegocio implements IProductoNegocio{

    private Logger log = LoggerFactory.getLogger(ProductoNegocio.class);

    @Autowired
    private ProductoRepository productoDAO;

    @Override
    public List<Producto> listado() throws NegocioException {
        try {
            return productoDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override
    public Producto cargar(long id) throws NegocioException, NoEncontradoException {
        Optional<Producto> o;
        try {
            o = productoDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
        if (!o.isPresent()) {
            throw new NoEncontradoException("No se encuentra el producto con id=" + id);
        }
        return o.get();
    }

    @Override
    public Producto agregar(Producto producto) throws NegocioException, EncontradoException {
        try {
            if(null!=findProductoByNombre(producto.getNombre()))
                throw new EncontradoException("Ya existe un producto con el nombre =" + producto.getNombre());
            cargar(producto.getId()); 									// tira excepcion sino no lo encuentra
            throw new EncontradoException("Ya existe un producto con id=" + producto.getId());
        } catch (NoEncontradoException e) {
        }
        try {
            return productoDAO.save(producto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    public Producto findProductoByNombre(String nombre) {
        return productoDAO.findByNombre(nombre).orElse(null);
    }


    @Override
    public Producto modificar(Producto producto) throws NegocioException, NoEncontradoException {
        cargar(producto.getId()); //Paso 1
        Producto productoWithNombre = findProductoByNombre(producto.getNombre());

        if(null!=productoWithNombre) { //Paso 2

            if (producto.getId() != productoWithNombre.getId())
                throw new NegocioException("Ya existe el producto " + productoWithNombre.getId() + "con el nombre ="
                        + producto.getNombre());	//Paso 3_a

            return	saveProducto(producto);	//Paso 3_b
        }

        return saveProducto(producto);	//Paso 4
    }

    private  Producto saveProducto(Producto producto) throws NegocioException {
        try {
            return productoDAO.save(producto); // sino existe el producto lo cargo
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override
    public void eliminar(long id) throws NegocioException, NoEncontradoException {
        cargar(id);
        try {
            productoDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }
}
