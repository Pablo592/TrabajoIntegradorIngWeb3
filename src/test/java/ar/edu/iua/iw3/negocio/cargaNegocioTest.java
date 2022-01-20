package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.Camion;
import ar.edu.iua.iw3.modelo.Carga;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.modelo.dto.CargaDTO;
import ar.edu.iua.iw3.modelo.persistencia.CargaRepository;
import ar.edu.iua.iw3.modelo.persistencia.OrdenRepository;
import ar.edu.iua.iw3.negocio.excepciones.*;
import ar.edu.iua.iw3.util.MensajeRespuesta;
import ar.edu.iua.iw3.util.RespuestaGenerica;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
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
    CargaNegocio cargaNegocio;

    @MockBean
    CargaRepository cargaDao;

    @MockBean
    OrdenRepository ordenDAO;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Autowired
    private static Carga cargaTest;

    @Autowired
    private static Orden orden;

    @Autowired
    private static Camion camion;

    @Autowired
    private static CargaDTO cargaDTO;

    @Autowired
    private static RespuestaGenerica<Carga>  respuesta;

    @BeforeClass
    public static void setup() throws ParseException {
        camion = new Camion();
        orden = new Orden();
        cargaTest = new Carga();
        orden.setEstado(2);
        camion.setPatente("ff888ff");
        orden.setCamion(camion);
        orden.setUmbralTemperaturaCombustible(5000);
        orden.setCodigoExterno("10");

        orden.setId(10);
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        cargaTest.setFechaSalidaHW(formato.parse("01/01/2000"));
        cargaTest.setFechaEntradaBackEnd(formato.parse("02/01/2000"));
        cargaTest.setCaudalLitroSegundo(54);
        cargaTest.setOrden(orden);
        camion.setPreset(5000);

        cargaTest.setTemperaturaProductoCelcius(10);
        cargaTest.setCaudalLitroSegundo(10);
        cargaTest.setDensidadProductoKilogramoMetroCub(544);
        cargaTest.setMasaAcumuladaKg(10);
        respuesta = new RespuestaGenerica<Carga>(cargaTest,new MensajeRespuesta());
        respuesta.setEntidad(cargaTest);
        cargaTest.getOrden().setCodigoExterno("10");
        cargaDTO = new CargaDTO();

        cargaDTO.setPromedDensidadProductoKilogramoMetroCub(545);
        cargaDTO.setPromedioCaudalLitroSegundo(45);
        cargaDTO.setPromedioTemperaturaProductoCelcius(10);
    }




    @Test
    public void testLoadSuccess2() throws NoEncontradoException, NegocioException {
        //given
        long id = 50;
        Optional<Carga> givenCarga = Optional.of(cargaTest);

        //when
        when(cargaDao.findById(id)).thenReturn(givenCarga);
        Carga cargaRecibida = cargaNegocio.cargar(id);

        //then
        assertEquals(10, cargaRecibida.getTemperaturaProductoCelcius());
        assertTrue(cargaRecibida.getDensidadProductoKilogramoMetroCub() == 544 );
    }


    @Ignore
    @Test
    public void testLoadSuccess3() throws NoEncontradoException, NegocioException, BadRequest, UnprocessableException, ConflictException {
        //given
        Optional<Orden> givenOrden = Optional.of(cargaTest.getOrden());
        givenOrden.get().setCodigoExterno("10");
        //when
        when(cargaDao.save(cargaTest)).thenReturn(cargaTest);
        when((cargaNegocio.getPromedioDensidadAndTemperaturaAndCaudal(orden.getCodigoExterno()))).thenReturn(cargaDTO);
        when(ordenDAO.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenDAO.findById(orden.getId())).thenReturn(givenOrden).thenReturn(givenOrden);
        RespuestaGenerica<Carga> cargaRecibida = cargaNegocio.agregar(cargaTest);

        //then
        assertEquals(0, cargaRecibida.getMensaje().getCodigo());
        assertTrue(cargaTest.equals(cargaRecibida.getEntidad()) );
    }



/*
    @Test
    public void testListSuccess()
            throws NegocioException, Exception {

        //given
        List<Producto> allProducts = new ArrayList<Producto>();
        allProducts.add(prod1);

//when
        when(productoBusiness.list()).thenReturn(allProducts);

//then
        mvc.perform(get("/api/v1/productos/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(prod1.getId())));
    }
*/
}
