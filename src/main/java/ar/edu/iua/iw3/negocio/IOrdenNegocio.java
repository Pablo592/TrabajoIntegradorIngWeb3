package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Camion;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;

import java.util.List;
import java.util.Optional;

public interface IOrdenNegocio {
    List<Orden> listado() throws NegocioException;

    Orden cargar(long id) throws NegocioException, NoEncontradoException;

    Orden agregar(Orden orden) throws NegocioException, EncontradoException;

    Orden modificar(Orden orden) throws NegocioException, NoEncontradoException;

    void eliminar(long id) throws NegocioException, NoEncontradoException;

    Orden findByCodigoExterno( String codigoExterno);

    Orden establecerPesajeInicial(Orden orden) throws NegocioException;

}
