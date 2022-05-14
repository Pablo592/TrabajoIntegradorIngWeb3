package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Alarma;
import ar.edu.iua.iw3.negocio.excepciones.*;

import java.util.List;

public interface IAlarmaNegocio {
    List<Alarma> listado() throws NegocioException;

    Alarma cargar(long id) throws NegocioException, NoEncontradoException;

    Alarma agregar(Alarma alarma) throws NegocioException, EncontradoException, BadRequest, NoEncontradoException;

    Alarma modificar(Alarma alarma) throws NegocioException, NoEncontradoException, ConflictException;

    List<Alarma> listarPorAutor(long id) throws NegocioException, NoEncontradoException;

    void eliminar(long id) throws NegocioException, NoEncontradoException;

}
