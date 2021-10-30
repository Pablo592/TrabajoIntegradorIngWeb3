package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Carga;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;

import java.util.List;

public interface ICargaNegocio {

    List<Carga> listado() throws NegocioException;

    Carga cargar(long id) throws NegocioException, NoEncontradoException;

    Carga agregar(Carga carga) throws NegocioException, EncontradoException;

    Carga modificar(Carga carga) throws NegocioException, NoEncontradoException;

    void eliminar(long id) throws NegocioException, NoEncontradoException;
}
