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

import java.util.List;
import java.util.Optional;

@Service
public class CargaNegocio implements ICargaNegocio {

    private Logger log = LoggerFactory.getLogger(CamionNegocio.class);

    @Autowired
    private CargaRepository cargaDAO;

    @Autowired
    private OrdenNegocio ordenNegocio;

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
        Orden orden = existeOrden(carga.getOrden().getCodigoExterno());
        try {
            if (orden.getEstado() == 2) {
                if (orden.getCamion().getPreset() <= carga.getMasaAcumuladaKg())
                    throw new NegocioException("Tanque lleno");
                carga.setOrden(orden);
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
        try{
            //calculo los datos de la orden y luego los actualizo
            return cargaDAO.getMasaAcuAndPromedioDensidadAndTemperaturaAndCaudal(orden.getId());
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
