package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Alarma;
import ar.edu.iua.iw3.negocio.excepciones.*;
import ar.edu.iua.iw3.util.RespuestaGenerica;

import java.util.List;

public interface IAlarmaNegocio {
    List<Alarma> listado() throws NegocioException;

    Alarma cargar(long id) throws NegocioException, NoEncontradoException;

    RespuestaGenerica<Alarma> agregar(Alarma alarma) throws NegocioException, EncontradoException, BadRequest, NoEncontradoException;

    RespuestaGenerica<Alarma> modificar(Alarma alarma) throws NegocioException, NoEncontradoException;

    List<Alarma> listarPorAutor(long id) throws NegocioException, NoEncontradoException;

    void eliminar(long id) throws NegocioException, NoEncontradoException;

    RespuestaGenerica<Alarma> aceptarAlarma(Alarma alarma) throws NegocioException, NoEncontradoException, ConflictException;;
}
