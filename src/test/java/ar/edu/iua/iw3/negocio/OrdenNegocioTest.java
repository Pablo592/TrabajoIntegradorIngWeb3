package ar.edu.iua.iw3.negocio;
import ar.edu.iua.iw3.modelo.*;
import ar.edu.iua.iw3.util.RespuestaGenerica;
import org.springframework.test.context.junit4.SpringRunner;
import ar.edu.iua.iw3.modelo.persistencia.OrdenRepository;
import ar.edu.iua.iw3.negocio.excepciones.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ActiveProfiles("mysqldev")
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrdenNegocioTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrdenNegocio ordenNegocio;

    @Autowired
    private OrdenRepository ordenDAO;

    private Orden orden3;
    private Orden orden2;
    private Orden orden ;
    private Cliente cliente ;
    private Chofer chofer;
    private Producto producto;
    private Camion camion;

    /*@MockBean
    OrdenRepository ordenDAO;*/

    @Before
    public void crearOrden(){
        orden = new Orden();
        orden.setCodigoExterno("45");
        orden.setFechaTurno(new Date());
        orden.setFrecuencia(30);
        ///////
        cliente = new Cliente();
        cliente.setRazonSocial("SA");
        cliente.setContacto(3512151243L);
        ///////
        chofer = new Chofer();
        chofer.setNombre("nuevo chofer nombre");
        chofer.setApellido("apellidochofer");
        chofer.setDocumento(40211001L);
        ///////
        producto = new Producto();
        producto.setNombre("nuevoproducto");
        ///////
        camion = new Camion();
        camion.setPatente("ad123as");
        camion.setCisternadoLitros(1000);
        camion.setPreset(3000);
        ///////
        orden.setCliente(cliente);
        orden.setChofer(chofer);
        orden.setProducto(producto);
        orden.setCamion(camion);
        orden.setCargaList(new ArrayList<Carga>());

    }
    @Test
    public void primerEnvio() throws EncontradoException, BadRequest, NegocioException, NoEncontradoException {
        ordenNegocio.agregar(orden);
        Orden ordenBD = ordenNegocio.findByCodigoExterno(orden.getCodigoExterno());
        if( ordenBD.getCodigoExterno().equals(orden.getCodigoExterno()) &&
                ordenBD.getEstado() == 1)
            System.out.println("Se creo la orden correctamente");
        else
            System.out.println("No se creo la orden correctamente");

        ordenNegocio.eliminar(ordenBD.getId());

        //assertThrows(NoEncontradoException.class, () -> ordenNegocio.findByCodigoExterno(orden.getCodigoExterno()));
    }

    @Test
    public void segundoEnvio() throws EncontradoException, BadRequest, NegocioException, ConflictException, NoEncontradoException {

        Orden o =  primero();
        Orden ordenConPesoInicial = ordenNegocio.cargar(orden2.getId());
        if(ordenConPesoInicial.getFechaPesajeInicial().getTime() == o.getFechaPesajeInicial().getTime())
            System.out.println("se guardo correctamente la fecha de pesaje");
        else
            System.out.println("No se creo la orden correctamente");

        ordenNegocio.eliminar(orden2.getId());
    }


    @Test
    public void tercerEnvio() throws EncontradoException, BadRequest, NegocioException, ConflictException, NoEncontradoException, UnprocessableException {

        Orden o =  primero();
        ordenNegocio.frenarCargar(o.getCodigoExterno());
        Orden ordenBD = ordenNegocio.findByCodigoExterno(o.getCodigoExterno());
        if(ordenBD.getEstado() == 3)
            System.out.println("Las cargas se detuvieron correctamente");
        else
            System.out.println("Las cargas no se detuvieron, tiene estado: " + ordenBD.getEstado());

        ordenNegocio.eliminar(ordenBD.getId());
    }

    @Test
    public void cuartoEnvio() throws EncontradoException, BadRequest, NegocioException, ConflictException, NoEncontradoException, UnprocessableException {

        Orden o =  segundo();

        o.setFechaRecepcionPesajeFinal(new Date());
        o.getCamion().setPesoFinalCamion(4541485456l);
        ordenNegocio.establecerPesajeFinal(o);
        Orden ordenBD = ordenNegocio.findByCodigoExterno(o.getCodigoExterno());
        if(ordenBD.getEstado() == 4)
            System.out.println("La orden se encuentra en estado 4");
        else
            System.out.println("Algo salio mal, tiene estado: " + ordenBD.getEstado());

        ordenNegocio.eliminar(ordenBD.getId());

    }

    private Orden primero() throws EncontradoException, BadRequest, NegocioException, ConflictException, NoEncontradoException {
        ordenNegocio.agregar(orden);
        Orden o = ordenNegocio.findByCodigoExterno(orden.getCodigoExterno());
        orden2 = o.clone();
        orden2.setFechaPesajeInicial(new Date());
        orden2.getCamion().setTara(1400);
        RespuestaGenerica<Orden> ordenBD = ordenNegocio.establecerPesajeInicial(orden2);

        return ordenBD.getEntidad();
    }

    private Orden segundo() throws EncontradoException, BadRequest, NegocioException, ConflictException, NoEncontradoException, UnprocessableException {
        Orden o =  primero();
        ordenNegocio.frenarCargar(o.getCodigoExterno());
        Orden ordenBD = ordenNegocio.findByCodigoExterno(o.getCodigoExterno());

        return ordenBD;
    }

}