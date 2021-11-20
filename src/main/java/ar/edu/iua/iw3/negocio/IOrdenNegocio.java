package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Carga;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;

import java.util.List;

public interface IOrdenNegocio {
    List<Orden> listado() throws NegocioException;

   // List<Orden> ultimaCarga(String codigoExterno) throws NegocioException;

    //Carga traerUltimaCarga(long codigoExterno) throws NegocioException, NoEncontradoException;

    Orden cargar(long id) throws NegocioException, NoEncontradoException;

    Orden agregar(Orden orden) throws NegocioException, EncontradoException;

    Orden modificar(Orden orden) throws NegocioException, NoEncontradoException;

    void eliminar(long id) throws NegocioException, NoEncontradoException;

    Orden establecerPesajeInicial(Orden orden) throws NegocioException, NoEncontradoException;

    Orden traerUltimaCarga(String codigoExterno) throws NegocioException, NoEncontradoException;
}
