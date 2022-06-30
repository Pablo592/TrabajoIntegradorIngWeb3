package ar.edu.iua.iw3.negocio;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.modelo.dto.ConciliacionDTO;
import ar.edu.iua.iw3.negocio.excepciones.*;
import ar.edu.iua.iw3.util.RespuestaGenerica;

import java.util.List;

public interface IOrdenNegocio {
    List<Orden> listado() throws NegocioException;

    Orden cargar(long id) throws NegocioException, NoEncontradoException;

    RespuestaGenerica<Orden> agregar(Orden orden) throws NegocioException, EncontradoException, BadRequest;

    Orden modificar(Orden orden) throws NegocioException, NoEncontradoException, ConflictException;

    void eliminar(long id) throws NegocioException, NoEncontradoException;

    RespuestaGenerica<Orden> establecerPesajeInicial(Orden orden) throws NegocioException, NoEncontradoException, BadRequest,UnprocessableException, ConflictException;

    RespuestaGenerica<Orden> frenarCargar(String codigoExterno) throws NegocioException, NoEncontradoException, UnprocessableException;

    RespuestaGenerica<Orden>  establecerPesajeFinal(Orden orden) throws NegocioException, NoEncontradoException, UnprocessableException;

    ConciliacionDTO  obtenerConciliacion(String codigoExterno) throws NegocioException, NoEncontradoException, UnprocessableException;

    public RespuestaGenerica<Orden> cambiarUmbralTemperatura(Orden orden) throws BadRequest, NoEncontradoException, NegocioException, ConflictException;

    public Orden findByCodigoExterno( String codigoExterno);
}

