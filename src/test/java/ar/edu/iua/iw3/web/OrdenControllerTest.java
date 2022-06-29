package ar.edu.iua.iw3.web;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ar.edu.iua.iw3.modelo.*;
import ar.edu.iua.iw3.modelo.Cuentas.Rol;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.modelo.dto.ConciliacionDTO;
import ar.edu.iua.iw3.negocio.OrdenNegocio;
import ar.edu.iua.iw3.security.authtoken.AuthToken;
import ar.edu.iua.iw3.util.MensajeRespuesta;
import ar.edu.iua.iw3.util.RespuestaGenerica;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
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

    @Before
    public  void setup_init() throws ParseException {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

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
    public void listSuccess() throws Exception {
        String token = getToken();

        //given
        List<Orden> ordenList= new ArrayList<Orden>();
        ordenList.add(orden);

        //when
        when(ordenNegocio.listado()).thenReturn(ordenList);

        //then
        mvc.perform(get("/test/api/v1/ordenes")
                        .param("xauthtoken", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].id",is(orden.getId()),Long.class));
    }


    @Test
    public void getConciliacionByCodigoExterno_Success() throws Exception {
        String token = getToken();
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
        mvc.perform(get("/test/api/v1/ordenes/conciliacion/" + orden.getCodigoExterno())
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

    //buscar una orden por id
    /*@Test
    public void agregarOrden_Success() throws Exception {
        String token = getToken();
        Gson gson = new Gson();
        String JSON = gson.toJson(orden);
        JSON = JSON.replaceAll("Jan 1, 2022, 12:00:00 AM" ,"2020-01-01");
        //given
        MensajeRespuesta m=new MensajeRespuesta();
        RespuestaGenerica<Orden> r = new RespuestaGenerica<Orden>(orden, m);

        //when
        when(ordenNegocio.agregar(orden)).thenReturn(r);

        //then
        mvc.perform(post("/test/api/v1/ordenes/primer-envio")
                        .param("xauthtoken", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
               .andExpect(status().isCreated());
    }*/


    @Test
    public void getOrdenById_Success() throws Exception {
        String token = getToken();
        //given

        //when
        when(ordenNegocio.cargar(orden.getId())).thenReturn(orden);
        //then
        mvc.perform(get("/test/api/v1/ordenes/buscar-una/" + orden.getId())
                        .param("xauthtoken", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$.id"),is(orden.getId()),Long.class))
                .andExpect(jsonPath(("$.codigoExterno"),is(orden.getCodigoExterno()),String.class))
                .andExpect(jsonPath(("$.frecuencia"),is(orden.getFrecuencia()),Integer.class));
    }

    //setear el peso inicial de un camion

    //eliminar

    private String getToken() {
        Rol admin = new Rol(1, "ROLE_USER", "Testing del sistema");
        Set<Rol> roles = new HashSet<Rol>();
        roles.add(admin);

        //int id, String nombre, String apellido, String email, String password, String username, Set<Rol> roles
        Usuario usuario1 = new Usuario(1,"Joel","Spítale","vspitale107@alumnos.iua.edu.ar","123","jspitale97",roles);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(usuario1, null,
                usuario1.getAuthorities());

        System.out.println("Autoridades = "+usuario1.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        Authentication auth1 = SecurityContextHolder.getContext().getAuthentication();
        usuario1 = (Usuario) auth1.getPrincipal();

        AuthToken newToken = new AuthToken(usuario1.getDuracionToken(), usuario1.getUsername());

        String token = newToken.encodeCookieValue();
        token = token.replace("[", "").replace("]", "");

        System.out.println("Token = "+token);

        return token;
    }
}
