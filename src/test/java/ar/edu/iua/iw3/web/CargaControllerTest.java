package ar.edu.iua.iw3.web;

import ar.edu.iua.iw3.modelo.*;
import ar.edu.iua.iw3.modelo.Cuentas.Rol;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.modelo.dto.CargaDTO;
import ar.edu.iua.iw3.negocio.CargaNegocio;
import ar.edu.iua.iw3.security.authtoken.AuthToken;
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

import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CargaControllerTest {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mvc;
    @MockBean
    private CargaNegocio cargaNegocio;
    private Orden orden;
    private Carga carga;
    private Cliente cliente;
    private Chofer chofer;
    private Producto producto;
    private Usuario usuario;
    private Camion camion;
    long idOrden = 1;
    long idCarga = 2;


    @Before
    public void setup_init() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        orden = new Orden();
        orden.setCodigoExterno("45");
        orden.setFechaTurno(new Date());
        orden.setFrecuencia(30);
        orden.setEstado(2);
        orden.setFechaPesajeInicial(new Date());
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
        camion.setTara(2000);
        ///////
        orden.setCliente(cliente);
        orden.setChofer(chofer);
        orden.setProducto(producto);
        orden.setCamion(camion);
        orden.setId(idOrden);
        //
        usuario = new Usuario();
        usuario.setUsername("admin");
        usuario.setPassword("123");
        //carga
        carga = new Carga();
        carga.setId(idCarga);
        carga.setMasaAcumuladaKg(10);
        carga.setDensidadProductoKilogramoMetroCub(454);
        carga.setTemperaturaProductoCelcius(25);
        carga.setCaudalLitroSegundo(3);
        carga.setFechaSalidaHW(new Date());
        carga.setOrden(orden);
        carga.setFechaEntradaBackEnd(new Date());

    }
    @Test
    public void listSuccess() throws Exception {
        String token = getToken();

        //given
        List<Carga> cargaList= new ArrayList<Carga>();
        cargaList.add(carga);

        //when
        when(cargaNegocio.listado()).thenReturn(cargaList);

        //then
        mvc.perform(get("/test/api/v1/cargas")
                        .param("xauthtoken", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].id",is(carga.getId()),Long.class));
    }

    @Test
    public void getOrdenById_Success() throws Exception {
        String token = getToken();
        float promedioCaudal = (float) 0.05;
        float promedioDensidad = 19;
        float promedioTemp = 1;

        //given
        CargaDTO cargaDTO = new CargaDTO(promedioDensidad,promedioTemp,promedioCaudal);
        //when
        when(cargaNegocio.getPromedioDensidadAndTemperaturaAndCaudal(orden.getCodigoExterno())).thenReturn(cargaDTO);
        //then
        mvc.perform(get("/test/api/v1/cargas/carga-promedio")
                        .param("xauthtoken", token)
                        .param("codigoExterno", orden.getCodigoExterno())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$.promedDensidadProductoKilogramoMetroCub"),is(promedioDensidad),Float.class))
                .andExpect(jsonPath(("$.promedioTemperaturaProductoCelcius"),is(promedioTemp),Float.class))
                .andExpect(jsonPath(("$.promedioCaudalLitroSegundo"),is(promedioCaudal),Float.class));
    }
    private String getToken() {
        Rol admin = new Rol(1, "ROLE_USER", "Testing del sistema");
        Set<Rol> roles = new HashSet<Rol>();
        roles.add(admin);

        //int id, String nombre, String apellido, String email, String password, String username, Set<Rol> roles
        Usuario usuario1 = new Usuario(1,"Joel","Spítale","vspitale107@alumnos.iua.edu.ar","123","jspitale97",roles);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(usuario1, null,usuario1.getAuthorities());

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
