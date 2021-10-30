package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Producto;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;

import java.util.List;

public interface IProductoNegocio {

    List<Producto> listado() throws NegocioException;

    Producto cargar(long id) throws NegocioException, NoEncontradoException;

    Producto agregar(Producto producto) throws NegocioException, EncontradoException;

    Producto modificar(Producto producto) throws NegocioException, NoEncontradoException;

    void eliminar(long id) throws NegocioException, NoEncontradoException;
}
