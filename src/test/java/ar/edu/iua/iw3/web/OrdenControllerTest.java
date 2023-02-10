package ar.edu.iua.iw3.web;

import static ar.edu.iua.iw3.util.Constantes.URL_ORDENES;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import ar.edu.iua.iw3.modelo.*;
import ar.edu.iua.iw3.modelo.Cuentas.Rol;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.modelo.dto.ConciliacionDTO;
import ar.edu.iua.iw3.negocio.OrdenNegocio;
import ar.edu.iua.iw3.security.authtoken.AuthToken;
import ar.edu.iua.iw3.util.Utilidades;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrdenControllerTest {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mvc;
    @MockBean
    private OrdenNegocio ordenNegocio;

    Orden orden;
    Cliente cliente ;
    Chofer chofer;
    Producto producto;
    Camion camion;
    long id = 1;
    private Utilidades utilidades;

    @Before
    public  void setup_init() throws ParseException {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        utilidades = new Utilidades();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaTurno = sdf.parse("2022-01-01");

        orden = new Orden();
        orden.setId(id);
        orden.setCodigoExterno("45");
        orden.setFechaTurno(fechaTurno);
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
    public void listarAllOrdenes_Success() throws Exception {
        String token = utilidades.getToken();

        //given
        List<Orden> ordenList= new ArrayList<Orden>();
        ordenList.add(orden);

        //when
        when(ordenNegocio.listado()).thenReturn(ordenList);

        //then
        mvc.perform(get("/"+URL_ORDENES)
                        .param("xauthtoken", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].id",is(orden.getId()),Long.class));
    }
    @Test
    public void getConciliacionByCodigoExterno_Success() throws Exception {
        String token = utilidades.getToken();
        float pesajeInicial = 1000;
        float pesajeFinal = 2000;
        float masaAcumuladaKg =1000;
        float netoPorBalanza = 0;
        float diferenciaNetoPorBalanza_masaAcumuludada = 1000;
        float promedDensidadProductoKilogramoMetroCub = (float) 0.874;
        float promedioTemperaturaProductoCelcius = 20;
        float promedioCaudalLitroSegundo = (float) 0.16;

        //given
        ConciliacionDTO conciliacionDTO = new ConciliacionDTO(pesajeInicial,pesajeFinal,masaAcumuladaKg,netoPorBalanza,diferenciaNetoPorBalanza_masaAcumuludada,promedDensidadProductoKilogramoMetroCub,promedioTemperaturaProductoCelcius,promedioCaudalLitroSegundo);
        //when
        when(ordenNegocio.obtenerConciliacion(orden.getCodigoExterno())).thenReturn(conciliacionDTO);
        //then
        mvc.perform(get("/"+URL_ORDENES+"/conciliacion/" + orden.getCodigoExterno())
                        .param("xauthtoken", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$.pesajeInicial"),is(conciliacionDTO.getPesajeInicial()),Float.class))
                .andExpect(jsonPath(("$.pesajeFinal"),is(conciliacionDTO.getPesajeFinal()),Float.class))
                .andExpect(jsonPath(("$.masaAcumuladaKg"),is(conciliacionDTO.getMasaAcumuladaKg()),Float.class))
                .andExpect(jsonPath(("$.netoPorBalanza"),is(conciliacionDTO.getNetoPorBalanza()),Float.class))
                .andExpect(jsonPath(("$.diferenciaNetoPorBalanza_masaAcumuludada"),is(conciliacionDTO.getDiferenciaNetoPorBalanza_masaAcumuludada()),Float.class))
                .andExpect(jsonPath(("$.promedDensidadProductoKilogramoMetroCub"),is(conciliacionDTO.getPromedDensidadProductoKilogramoMetroCub()),Float.class))
                .andExpect(jsonPath(("$.promedioTemperaturaProductoCelcius"),is(conciliacionDTO.getPromedioTemperaturaProductoCelcius()),Float.class))
                .andExpect(jsonPath(("$.promedioCaudalLitroSegundo"),is(conciliacionDTO.getPromedioCaudalLitroSegundo()),Float.class));
    }
    /*@Test
    public void agregarOrden_Success() throws Exception {
        String token = getToken();
        Orden orden1 = new Orden();
        orden1.setId(14);
        orden1.setFechaTurno(new Date());
        orden1.setFrecuencia(30);
        orden1.setCamion(camion);
        orden1.setCliente(cliente);
        orden1.setChofer(chofer);
        orden1.setProducto(producto);

       //given
        MensajeRespuesta m=new MensajeRespuesta();
        RespuestaGenerica<Orden> r = new RespuestaGenerica<Orden>(orden1, m);


       //when
        when(ordenNegocio.agregar(orden1)).thenReturn(r);

        //then
        mvc.perform(post("/test/api/v1/ordenes/crear")
                        .param("xauthtoken", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(orden1)).accept(MediaType.APPLICATION_JSON)
                       )
                .andDo(print())
               .andExpect(status().isCreated());

    }
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    */
    @Test
    public void getOrdenById_Success() throws Exception {
        String token = utilidades.getToken();
        //given

        //when
        when(ordenNegocio.cargar(orden.getId())).thenReturn(orden);
        //then
        mvc.perform(get("/"+URL_ORDENES+"/" + orden.getId())
                        .param("xauthtoken", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$.id"),is(orden.getId()),Long.class))
                .andExpect(jsonPath(("$.codigoExterno"),is(orden.getCodigoExterno()),String.class))
                .andExpect(jsonPath(("$.frecuencia"),is(orden.getFrecuencia()),Integer.class));
    }

}
