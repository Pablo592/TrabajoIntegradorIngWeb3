package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Alarma;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.negocio.excepciones.BadRequest;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
import java.util.List;

public interface AlarmaNegocio {
    List<Alarma> listado() throws NegocioException;

    Alarma cargar(long id) throws NegocioException, NoEncontradoException;

    Alarma agregar(Alarma alarma) throws NegocioException, EncontradoException, BadRequest;

    Alarma modificar(Alarma alarma) throws NegocioException, NoEncontradoException;

    void eliminar(long id) throws NegocioException, NoEncontradoException;

    List<Alarma> listadoAlarmasByUser(Usuario usuario) throws NegocioException;

    List<Alarma> listadoAlarmasByUserAndOrden(Usuario usuario,String codigoExterno) throws NegocioException;
}
