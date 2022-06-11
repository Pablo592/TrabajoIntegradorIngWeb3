package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.*;
import ar.edu.iua.iw3.modelo.Cuentas.IUsuarioNegocio;
import ar.edu.iua.iw3.modelo.Cuentas.Rol;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.modelo.Cuentas.UsuarioRepository;
import ar.edu.iua.iw3.modelo.dto.CargaDTO;
import ar.edu.iua.iw3.modelo.persistencia.CargaRepository;
import ar.edu.iua.iw3.modelo.persistencia.OrdenRepository;
import ar.edu.iua.iw3.negocio.excepciones.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CargaNegocioTest {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private Orden orden;
    private Carga carga;
    private Cliente cliente;
    private Chofer chofer;
    private Producto producto;

    private Usuario usuario;
    private Camion camion;
    long idOrden = 1;
    long idCarga = 2;

    @MockBean
    CargaRepository cargaRepositoryMock;
    @Autowired
    CargaNegocio cargaNegocio;

    @MockBean
    OrdenRepository ordenRepositoryMock;
    @Autowired
    OrdenNegocio ordenNegocio;

    @MockBean
    UsuarioRepository usuarioRepositoryMock;

    @Autowired
    private IUsuarioNegocio userBusiness;

    @Before
    public void setup_init() {
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
        camion.setTara(5000);
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
    }
    @Test//tercer envio caso feliz
    public void agregarCargar() throws BadRequest, EncontradoException, UnprocessableException, ConflictException, NoEncontradoException, NegocioException {
        carga = new Carga();
        carga.setMasaAcumuladaKg(10);
        carga.setDensidadProductoKilogramoMetroCub(454);
        carga.setTemperaturaProductoCelcius(25);
        carga.setCaudalLitroSegundo(3);
        carga.setFechaSalidaHW(new Date());
        carga.setOrden(orden);
        carga.setId(idCarga);

        carga.setFechaEntradaBackEnd(new Date());

        //given
        Optional<Orden> givenOrden = Optional.of(orden);

        //when+
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(cargaRepositoryMock.save(carga)).thenReturn(carga);

        Carga carga1 = cargaNegocio.agregar(carga).getEntidad();

        //then
        assertEquals(idCarga,carga1.getId());
    }
    @Test
    public void enviarCargaConHoraHadwareDespuesDeHoraLlegadaBackend(){
        carga = new Carga();
        carga.setMasaAcumuladaKg(10);
        carga.setDensidadProductoKilogramoMetroCub(454);
        carga.setTemperaturaProductoCelcius(25);
        carga.setCaudalLitroSegundo(3);
        carga.setFechaSalidaHW(new Date());
        carga.setOrden(orden);
        carga.setId(idCarga);

        Date fechaEntradaBackEnd = new Date();

        try {
            fechaEntradaBackEnd = new SimpleDateFormat("yyyy-MM-dd").parse("2022-06-02");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        carga.setFechaEntradaBackEnd(fechaEntradaBackEnd);

        //given
        Optional<Orden> givenOrden = Optional.of(orden);

        //when+
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(cargaRepositoryMock.save(carga)).thenReturn(carga);

        //then
        assertThrows(ConflictException.class, () -> cargaNegocio.agregar(carga));
    }


    @Test
    public void enviarCargaConOrdenEnEstadoIncorrecto(){
        carga = new Carga();
        carga.setMasaAcumuladaKg(10);
        carga.setDensidadProductoKilogramoMetroCub(454);
        carga.setTemperaturaProductoCelcius(25);
        carga.setCaudalLitroSegundo(3);
        carga.setFechaSalidaHW(new Date());
        carga.setOrden(orden);
        carga.setId(idCarga);
        carga.getOrden().setEstado(1);

        carga.setFechaEntradaBackEnd(new Date());

        //given
        Optional<Orden> givenOrden = Optional.of(orden);

        //when+
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(cargaRepositoryMock.save(carga)).thenReturn(carga);

        //then
        assertThrows(UnprocessableException.class, () -> cargaNegocio.agregar(carga));
    }

    @Test
    public void enviarCargasConsecutivasConMismaMasaAcumulada(){
        carga = new Carga();
        carga.setMasaAcumuladaKg(10);
        carga.setDensidadProductoKilogramoMetroCub(454);
        carga.setTemperaturaProductoCelcius(25);
        carga.setCaudalLitroSegundo(3);
        carga.setFechaSalidaHW(new Date());
        carga.setOrden(orden);
        carga.setId(idCarga);
        carga.setFechaEntradaBackEnd(new Date());

        carga.setOrden(orden);
        orden.getCargaList().add(carga);
        orden.setMasaAcumuladaKg(carga.getMasaAcumuladaKg());


        CargaDTO cargaDTO = new CargaDTO(carga.getDensidadProductoKilogramoMetroCub(),carga.getTemperaturaProductoCelcius(),carga.getCaudalLitroSegundo());


        //given
        Optional<Orden> givenOrden = Optional.of(orden);

        //when+
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(cargaRepositoryMock.save(carga)).thenReturn(carga);
        when(cargaRepositoryMock.getPromedioDensidadAndTemperaturaAndCaudal(orden.getId())).thenReturn(cargaDTO);   //se lo agrege
        assertThrows(UnprocessableException.class, () -> cargaNegocio.agregar(carga));
    }

    @Test
    public void envioDeAlarmaUnicaVez() throws BadRequest, EncontradoException, UnprocessableException, ConflictException, NoEncontradoException, NegocioException {
        carga = new Carga();
        carga.setMasaAcumuladaKg(10);
        carga.setDensidadProductoKilogramoMetroCub(454);
        carga.setTemperaturaProductoCelcius(26);
        carga.setCaudalLitroSegundo(3);
        carga.setFechaSalidaHW(new Date());
        carga.setOrden(orden);
        carga.setId(idCarga);
        carga.setFechaEntradaBackEnd(new Date());
        //los roles del usuario
        Rol rol = new Rol();
        rol.setNombre("ROLE_ADMIN");
        Set<Rol> roles = new HashSet<>();
        roles.add(rol);
        usuario.setRoles(roles);

        Optional<Usuario> givenUsuario = Optional.of(usuario);

        when(usuarioRepositoryMock.findFirstByUsernameOrEmail(usuario.getUsername(), usuario.getUsername())).thenReturn(Optional.ofNullable(usuario));
        userBusiness.cargarPorUsernameOEmail(usuario.getUsername());



        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        //given
        Optional<Orden> givenOrden = Optional.of(orden);

        //when

        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(cargaRepositoryMock.save(carga)).thenReturn(carga);

        //tengo que agregar el repository de las alarmar
        cargaNegocio.agregar(carga);
        System.out.println(orden.isEnviarMailActivo());

        //assertThrows(UnprocessableException.class, () -> cargaNegocio.agregar(carga));
    }
}
