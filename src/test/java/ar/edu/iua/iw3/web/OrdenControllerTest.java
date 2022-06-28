package ar.edu.iua.iw3.web;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import ar.edu.iua.iw3.modelo.Cuentas.Rol;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.modelo.dto.ConciliacionDTO;
import ar.edu.iua.iw3.negocio.OrdenNegocio;
import ar.edu.iua.iw3.security.authtoken.AuthToken;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
    long id = 1;

    @Before
    public  void setup_init() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        orden = new Orden();
        orden.setId(id);
        orden.setCodigoExterno("45");
        orden.setFechaTurno(new Date());
        orden.setFrecuencia(30);
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
    public void listOrdenById_Success() throws Exception {
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
                        .andExpect(jsonPath(("$.pesajeInicial"),is(conciliacionDTO.getPesajeInicial()),Float.class));
    }

    //buscar una orden por id

    //obtener una concilicacion

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
