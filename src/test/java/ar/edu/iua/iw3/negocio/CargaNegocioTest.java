package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.*;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.modelo.Cuentas.UsuarioRepository;
import ar.edu.iua.iw3.modelo.dto.CargaDTO;
import ar.edu.iua.iw3.modelo.persistencia.AlarmaRepository;
import ar.edu.iua.iw3.modelo.persistencia.CargaRepository;
import ar.edu.iua.iw3.modelo.persistencia.OrdenRepository;
import ar.edu.iua.iw3.negocio.excepciones.*;
import ar.edu.iua.iw3.util.MensajeRespuesta;
import ar.edu.iua.iw3.util.RespuestaGenerica;
import ar.edu.iua.iw3.util.Utilidades;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
@RunWith(SpringRunner.class)
@SpringBootTest
public class CargaNegocioTest {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @MockBean
    CargaRepository cargaRepositoryMock;
    @Autowired
    CargaNegocio cargaNegocio;
    @MockBean
    OrdenRepository ordenRepositoryMock;
    @Autowired
    OrdenNegocio ordenNegocio;
    @MockBean
    AlarmaRepository alarmaRepositoryMock;
    @MockBean
    AlarmaNegocio alarmaNegocioMock;
    @MockBean
    UsuarioRepository usuarioRepositoryMock;
    private Orden orden;
    private Carga carga;
    private Cliente cliente;
    private Chofer chofer;
    private Producto producto;
    private Usuario usuario;
    private Camion camion;
    long idOrden = 1;
    long idCarga = 2;
    private Utilidades utilidades;
    @Before
    public void setup_init() {
        utilidades = new Utilidades();
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
        carga.setMasaAcumuladaKg(10);
        carga.setDensidadProductoKilogramoMetroCub(454);
        carga.setTemperaturaProductoCelcius(25);
        carga.setCaudalLitroSegundo(3);
        carga.setFechaSalidaHW(new Date());
        carga.setOrden(orden);
        carga.setId(idCarga);
        carga.setFechaEntradaBackEnd(new Date());

    }
    @Test//tercer envio caso feliz
    public void crearCarga_Success() throws BadRequest, UnprocessableException, ConflictException, NoEncontradoException, NegocioException {
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
    public void intentarCrearCargaConHoraHadwareDespuesDeHoraLlegadaBackend_ConflictException(){
        Date fechaEntradaBackEnd;
        try {
            fechaEntradaBackEnd = new SimpleDateFormat("yyyy-MM-dd").parse("2022-06-02");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        carga.setFechaEntradaBackEnd(fechaEntradaBackEnd);
        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(cargaRepositoryMock.save(carga)).thenReturn(carga);
        //then
        assertThrows(ConflictException.class, () -> cargaNegocio.agregar(carga));
    }
    @Test
    public void crearCargaConOrdenEnEstadoIncorrecto_UnprocessableException(){
        carga.getOrden().setEstado(1);
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
    public void crearCargasConsecutivasConMismaMasaAcumulada_UnprocessableException(){
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
        when(cargaRepositoryMock.getPromedioDensidadAndTemperaturaAndCaudal(orden.getId())).thenReturn(cargaDTO);
        assertThrows(UnprocessableException.class, () -> cargaNegocio.agregar(carga));
    }
    @Test
    public void generarUnicaAlarmaYEnviarMail_Success() throws BadRequest, UnprocessableException, ConflictException, NoEncontradoException, NegocioException {
        carga.setTemperaturaProductoCelcius(26);
        utilidades.getToken();
        //alarma
        Authentication auth_aux = SecurityContextHolder.getContext().getAuthentication();
        Usuario user = (Usuario) auth_aux.getPrincipal();

        Alarma a = new Alarma();
        a.setOrdenAlarma(orden);
        a.setAutor(user);
        a.setDescripcion("Humbral de temperatura superado de la orden (codigo externo) " + orden.getCodigoExterno() + " con una temperatura de " + carga.getTemperaturaProductoCelcius());

        MensajeRespuesta m = new MensajeRespuesta();
        RespuestaGenerica<Alarma> r = new RespuestaGenerica<Alarma>(a, m);
        m.setCodigo(0);
        m.setMensaje(a.toString());
        when(alarmaNegocioMock.agregar(a)).thenReturn(r);
        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(cargaRepositoryMock.save(carga)).thenReturn(carga);

        assertNotEquals(-1, cargaNegocio.agregar(carga).getMensaje().getCodigo());
        assertEquals(true, orden.isEnviarMailActivo());
    }
    @Test
    public void intentarCargarContanqueLleno_UnprocessableException() {
        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        carga.setMasaAcumuladaKg((float) (camion.getPreset()+1));
        //when
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(cargaRepositoryMock.save(carga)).thenReturn(carga);
        //then
        assertThrows(UnprocessableException.class, () -> cargaNegocio.agregar(carga));
    }

}
