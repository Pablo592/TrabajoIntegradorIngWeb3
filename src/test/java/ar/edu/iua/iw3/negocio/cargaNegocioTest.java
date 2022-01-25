package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.*;
import ar.edu.iua.iw3.modelo.dto.CargaDTO;
import ar.edu.iua.iw3.modelo.persistencia.CargaRepository;
import ar.edu.iua.iw3.modelo.persistencia.OrdenRepository;
import ar.edu.iua.iw3.negocio.excepciones.*;
import ar.edu.iua.iw3.util.MensajeRespuesta;
import ar.edu.iua.iw3.util.RespuestaGenerica;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ActiveProfiles("mysqldev")
@RunWith(SpringRunner.class)
@SpringBootTest
public class cargaNegocioTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrdenNegocio ordenNegocio;

    @Autowired
    private CargaNegocio cargaNegocio;


    @Autowired
    private OrdenRepository ordenDAO;

    private Orden orden3;
    private Orden orden2;
    private Orden orden;
    private Cliente cliente;
    private Chofer chofer;
    private Producto producto;
    private Camion camion;
    private Carga carga;


    /*@MockBean
    OrdenRepository ordenDAO;*/

    @Before
    public void crearOrden() {
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
        //orden.setCargaList(new ArrayList<Carga>());



        carga = new Carga();
        carga.setMasaAcumuladaKg(10);
        carga.setDensidadProductoKilogramoMetroCub(454);
        carga.setTemperaturaProductoCelcius(25);
        carga.setCaudalLitroSegundo(3);
        carga.setFechaSalidaHW(new Date());

        /*{"orden":{
   "codigoExterno": "454"
},
"masaAcumuladaKg": {{masaAcumuladaKg}},
"densidadProductoKilogramoMetroCub": 0.85,
"temperaturaProductoCelcius": 25.0,
"caudalLitroSegundo": 0.3,
"fechaSalidaHW":"{{tiempoOutHw}}"
}*/
    }


  /*  @Test
    public void testLoadSuccess2() throws NoEncontradoException, NegocioException {
        //given
        long id = 50;
        Optional<Carga> givenCarga = Optional.of(cargaTest);

        //when
        when(cargaDao.findById(id)).thenReturn(givenCarga);
        Carga cargaRecibida = cargaNegocio.cargar(id);

        //then
        assertEquals(10, cargaRecibida.getTemperaturaProductoCelcius());
        assertTrue(cargaRecibida.getDensidadProductoKilogramoMetroCub() == 544);
    }

*/

    @Test
    public void testLoadSuccess2() throws NoEncontradoException, NegocioException, EncontradoException, BadRequest, ConflictException, UnprocessableException {

     String codExt =  establecerOrdenEnEstadoDos();


     carga.setFechaEntradaBackEnd(new Date());
     carga.setOrden(new Orden());
     carga.getOrden().setCodigoExterno(codExt);
     cargaNegocio.agregar(carga);
    }


    public String establecerOrdenEnEstadoDos() throws EncontradoException, BadRequest, NegocioException, ConflictException, NoEncontradoException {
        ordenNegocio.agregar(orden);
        Orden o = ordenNegocio.findByCodigoExterno(orden.getCodigoExterno());
        orden2 = o.clone();
        orden2.setFechaPesajeInicial(new Date());
        orden2.getCamion().setTara(1400);
        RespuestaGenerica<Orden> ordenBD = ordenNegocio.establecerPesajeInicial(orden2);    //estado 2
        o = ordenBD.getEntidad();
        Orden ordenConPesoInicial = ordenNegocio.cargar(orden2.getId());
        return ordenConPesoInicial.getCodigoExterno();
    }
}