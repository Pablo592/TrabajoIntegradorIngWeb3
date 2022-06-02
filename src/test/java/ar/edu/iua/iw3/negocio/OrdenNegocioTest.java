package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.*;
import ar.edu.iua.iw3.modelo.persistencia.OrdenRepository;
import ar.edu.iua.iw3.negocio.excepciones.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
@RunWith(SpringRunner.class)
//@ActiveProfiles("mysqldev")
@SpringBootTest //lo utilizo para indicar que son test de negocio
public class OrdenNegocioTest {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private  Orden orden;
    private Cliente cliente ;
    private Chofer chofer;
    private Producto producto;
    private Camion camion;

    long id = 1;

    @MockBean
    OrdenRepository ordenRepositoryMock;
    @Autowired
    OrdenNegocio ordenNegocio;

    @Before
    public  void setup_init() {
        orden = new Orden();
        orden.setId(id);
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
    public void buscarOrdenPorCodigoExterno() {
        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findByCodigoExterno("45")).thenReturn(givenOrden);
        Orden ordenBuscada = ordenNegocio.findByCodigoExterno("45");
        //then
        assertEquals(id,ordenBuscada.getId());
        assertEquals("45",ordenBuscada.getCodigoExterno());
        assertEquals(30,ordenBuscada.getFrecuencia());
    }

    @Test
    public void buscarOrdenPorId() throws NoEncontradoException, NegocioException {
        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findById(id)).thenReturn(givenOrden);
        Orden ordenBuscada = ordenNegocio.cargar(id);
        //then
        assertEquals(id,ordenBuscada.getId());
        assertEquals("45",ordenBuscada.getCodigoExterno());
        assertEquals(30,ordenBuscada.getFrecuencia());
        assertEquals(cliente.getRazonSocial(),ordenBuscada.getCliente().getRazonSocial());
        assertEquals(chofer.getDocumento(),ordenBuscada.getChofer().getDocumento());
        assertEquals(producto.getNombre(),ordenBuscada.getProducto().getNombre());
        assertEquals(camion.getPatente(),ordenBuscada.getCamion().getPatente());
    }

    //primer envio
    @Test //caso feliz
    public void crearOrdenConMetadatosSuficientes() throws EncontradoException, BadRequest, NegocioException {
        //when + given
        when(ordenRepositoryMock.save(orden)).thenReturn(orden);
        Orden ordenCreada = ordenNegocio.agregar(orden).getEntidad();
        //then
        assertEquals(ordenCreada.getCodigoExterno(),orden.getCodigoExterno());
        assertEquals(1,ordenCreada.getEstado());
    }

    @Test
    public void crearOrdenSinCamionAsociado() {
        orden.setCamion(new Camion());
        //when + given + then
        when(ordenRepositoryMock.save(orden)).thenReturn(orden);
        assertThrows(BadRequest.class, () -> ordenNegocio.agregar(orden));
        //System.out.println(ordenNegocio.agregar(orden).getMensaje().getMensaje()); --> Se debe completar el campo 'patente'
    }

    @Test
    public void crearOrdenSinClienteAsociado() {
        orden.setCliente(new Cliente());
        //when + given + then
        when(ordenRepositoryMock.save(orden)).thenReturn(orden);
        assertThrows(BadRequest.class, () -> ordenNegocio.agregar(orden));
        //System.out.println(ordenNegocio.agregar(orden).getMensaje().getMensaje()); //El atributo 'Razon Social' es obligatorio
    }

    @Test
    public void crearOrdenSinChoferAsociado(){
        orden.setChofer(new Chofer());
        //when + given + then
        when(ordenRepositoryMock.save(orden)).thenReturn(orden);
        assertThrows(BadRequest.class, () -> ordenNegocio.agregar(orden));
        //System.out.println(ordenNegocio.agregar(orden).getMensaje().getMensaje()); //El atributo 'nombre' es obligatorio,etc
    }

    @Test
    public void crearOrdenSinProductoAsociado() {
        orden.setProducto(new Producto());
        //when + given + then
        when(ordenRepositoryMock.save(orden)).thenReturn(orden);
        assertThrows(BadRequest.class, () -> ordenNegocio.agregar(orden));
        //System.out.println(ordenNegocio.agregar(orden).getMensaje().getMensaje()); //El atributo 'Producto' es obligatorio
    }

    //segundo request
    @Test //caso feliz
    public void setearFechaPesajeInicial() throws BadRequest, ConflictException, NoEncontradoException, NegocioException {
        double taraCamion = 5000;
        Date fechaPesoInicial = new Date();
        orden.setEstado(1);
        orden.setFechaPesajeInicial(fechaPesoInicial);
        orden.getCamion().setTara(taraCamion);

        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);

        Orden orden1 = ordenNegocio.establecerPesajeInicial(orden).getEntidad();
        //then
        assertEquals(id,orden1.getId());
        assertEquals("45",orden1.getCodigoExterno());
        assertEquals(taraCamion,orden1.getCamion().getTara());
        assertEquals(fechaPesoInicial,orden1.getFechaPesajeInicial());
    }


    @Test
    public void segundoEnvioSinTara()  {
        Date fechaPesoInicial = new Date();
        orden.setFechaPesajeInicial(fechaPesoInicial);
        orden.setEstado(1);
        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);

        //then
        assertThrows(BadRequest.class, () -> ordenNegocio.establecerPesajeInicial(orden));

        //System.out.println(ordenNegocio.establecerPesajeInicial(orden).getMensaje().getMensaje()); //El atributo 'tara' tiene que ser mayor a cero
    }

    @Test
    public void segundoEnvioSinFechaPesajeInicial() {
        double taraCamion = 5000;
        orden.getCamion().setTara(taraCamion);
        orden.setEstado(1);
        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);

        //then
        assertThrows(BadRequest.class, () -> ordenNegocio.establecerPesajeInicial(orden));

        //System.out.println(ordenNegocio.establecerPesajeInicial(orden).getMensaje().getMensaje()); //El atributo 'fechaPesajeInicial' debe poseer un valor
    }

    @Test
    public void segundoEnvioSinPatente() throws BadRequest, ConflictException, NoEncontradoException, NegocioException {
        double taraCamion = 5000;
        Date fechaPesoInicial = new Date();
        orden.setFechaPesajeInicial(fechaPesoInicial);
        orden.setCamion(new Camion());  //borro la patente
        orden.getCamion().setTara(taraCamion);
        orden.setEstado(1);
        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        //then
        assertThrows(BadRequest.class, () -> ordenNegocio.establecerPesajeInicial(orden));

        //System.out.println(ordenNegocio.establecerPesajeInicial(orden).getMensaje().getMensaje()); //El atributo 'Patente' es obligatorio
    }

    @Test
    public void segundoEnvioConEstadoOrdenDiferenteDe1() throws BadRequest, ConflictException, NoEncontradoException, NegocioException {
        double taraCamion = 5000;
        Date fechaPesoInicial = new Date();
        orden.setFechaPesajeInicial(fechaPesoInicial);
        orden.getCamion().setPatente("aa000wa");    //se lo seteo porque sino me lo intenta guardar
        orden.getCamion().setTara(taraCamion);
        orden.setEstado(0);
        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        //then
        assertThrows(ConflictException.class, () -> ordenNegocio.establecerPesajeInicial(orden));

        //System.out.println(ordenNegocio.establecerPesajeInicial(orden).getMensaje().getMensaje()); //Solo se puede pasar a estado 2 las ordenes en estado 1
    }


}
