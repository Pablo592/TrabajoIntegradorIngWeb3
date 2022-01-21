package ar.edu.iua.iw3.negocio;

<<<<<<< HEAD
public class OrdenNegocioTest {
=======
import ar.edu.iua.iw3.modelo.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import ar.edu.iua.iw3.modelo.persistencia.OrdenRepository;
import ar.edu.iua.iw3.negocio.excepciones.*;
import ar.edu.iua.iw3.util.MensajeRespuesta;
import ar.edu.iua.iw3.util.RespuestaGenerica;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@ActiveProfiles("mysqldev")
@RunWith(SpringRunner.class)
@SpringBootTest
public class OrdenNegocioTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrdenNegocio ordenNegocio;

    @Autowired
    private OrdenRepository ordenDAO;

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
        orden.setCodigoExterno("1");
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
       ordenNegocio.agregar(orden);
       Orden ordenBD = ordenNegocio.findByCodigoExterno(orden.getCodigoExterno());
       Date date = new Date();
       ordenBD.setFechaPesajeInicial(date);
       Orden ordenConPesoInicial = ordenNegocio.modificar(ordenBD);
       if(ordenConPesoInicial.getFechaPesajeInicial().equals(date))
           System.out.println("se guardo correctamente la fecha de pesaje");
       else
           System.out.println("No se creo la orden correctamente");

        ordenNegocio.eliminar(ordenBD.getId());
    }




>>>>>>> 65b856d42a0fdb2fcd3f82b39f880bc998a750ac
}
