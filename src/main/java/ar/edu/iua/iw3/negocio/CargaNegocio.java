package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Carga;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.modelo.dto.CargaDTO;
import ar.edu.iua.iw3.modelo.persistencia.CargaRepository;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CargaNegocio implements ICargaNegocio {

    private Logger log = LoggerFactory.getLogger(CamionNegocio.class);

    @Autowired
    private CargaRepository cargaDAO;

    @Autowired
    private OrdenNegocio ordenNegocio;
    @Autowired
    private CamionNegocio camionNegocio;

    @Override
    public List<Carga> listado() throws NegocioException {
        try {
            return cargaDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }


    @Override
    public Carga traerUltimaCarga(String codigoExterno) throws NegocioException, NoEncontradoException {
        Orden orden = ordenNegocio.findByCodigoExterno(codigoExterno);
        if(null==orden)
            throw new NoEncontradoException("El codigo externo no pertenece a ninguna orden");
        try {
            return cargaDAO.findTheLastCarga(orden.getId());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }

    }
    @Override
    public Carga cargar(long id) throws NegocioException, NoEncontradoException {
        Optional<Carga> o;
        try {
            o = cargaDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
        if (!o.isPresent()) {
            throw new NoEncontradoException("No se encuentra la carga con id=" + id);
        }
        return o.get();
    }

    @Override   //cargo todo lo que venga
    public Carga agregar(Carga carga) throws NegocioException, NoEncontradoException {
        String codigoExterno = carga.getOrden().getCodigoExterno();
        Orden orden = existeOrden(codigoExterno);
        try {
            if (orden.getEstado() == 2) {
                if (orden.getCamion().getPreset() <= carga.getMasaAcumuladaKg()){
                    orden.setEstado(3);
                    ordenNegocio.modificar(orden);
                    throw new NegocioException("Tanque lleno");
                }
                carga.setOrden(orden);
                //obtengo y guardo los promedios de las cargas lo tengo que pones porque sino hay cargas cargadas entonces no hay promedio
                try {
                    CargaDTO cargaDTO = getAcumulacionAndPromedioCargas(codigoExterno);
                    orden.setPromedDensidadProductoKilogramoMetroCub(cargaDTO.getPromedDensidadProductoKilogramoMetroCub());
                    orden.setPromedioCaudalLitroSegundo(cargaDTO.getPromedioCaudalLitroSegundo());
                    orden.setPromedioTemperaturaProductoCelcius(cargaDTO.getPromedioTemperaturaProductoCelcius());

                }catch (Exception e){
                    log.error(e.getMessage(), e);
                }
                //seteo la masa acumulada
                orden.setMasaAcumuladaKg(carga.getMasaAcumuladaKg());
                orden.setUltimoCaudalLitroSegundo(carga.getCaudalLitroSegundo());
                orden.setUltimaDensidadProductoKilogramoMetroCub(carga.getDensidadProductoKilogramoMetroCub());
                orden.setUltimaTemperaturaProductoCelcius(carga.getTemperaturaProductoCelcius());
                orden.setFechaFinProcesoCarga(new Date());


                if(orden.getFechaInicioProcesoCarga() == null)
                    orden.setFechaInicioProcesoCarga(new Date());

                ordenNegocio.modificar(orden);
                //transaccion para que se setee la ultima masa acumulada de orden
                return cargaDAO.save(carga);
            }else
                throw new NegocioException("La orden : "
                        + orden.getCodigoExterno()+" no esta en estado 2, por lo que no puede realizar la carga");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    private Orden existeOrden(String codigoExterno) throws NoEncontradoException {
        Orden orden = ordenNegocio.findByCodigoExterno(codigoExterno);
        if(null == orden )
            throw new NoEncontradoException("No existe una orden con codigo externo: "
                    + codigoExterno );
        return orden;
    }

    @Override
    public CargaDTO getAcumulacionAndPromedioCargas(String codigoExterno) throws NegocioException, NoEncontradoException {
        Orden orden = existeOrden(codigoExterno);
        if(orden.getCargaList().size() ==0)
            throw new NegocioException("No hay cargas registradas en esta orden");

        try{
            //calculo los datos de la orden y luego los actualizo
            return cargaDAO.getPromedioDensidadAndTemperaturaAndCaudal(orden.getId());
        }catch (Exception e){
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }


    }

    @Override
    public Carga modificar(Carga carga) throws NegocioException, NoEncontradoException {
        cargar(carga.getId());
        return saveCarga(carga);
    }


    private  Carga saveCarga(Carga carga) throws NegocioException {
        try {
            return cargaDAO.save(carga); // sino existe la carga lo cargo
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }

    @Override
    public void eliminar(long id) throws NegocioException, NoEncontradoException {
        cargar(id);
        try {
            cargaDAO.deleteById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new NegocioException(e);
        }
    }
}
