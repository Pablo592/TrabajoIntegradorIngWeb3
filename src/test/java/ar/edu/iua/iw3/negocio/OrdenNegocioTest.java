package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.*;
import ar.edu.iua.iw3.modelo.dto.ConciliacionDTO;
import ar.edu.iua.iw3.modelo.persistencia.CamionRepository;
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
@SpringBootTest //lo utilizo para indicar que son test de negocio
public class OrdenNegocioTest {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private  Orden orden;
    private Cliente cliente ;
    private Chofer chofer;
    private Producto producto;
    private Camion camion;
    private Carga carga;

    long id = 1;

    @MockBean
    OrdenRepository ordenRepositoryMock;
    @MockBean
    CamionNegocio camionNegocioMock;
    @MockBean
    CamionRepository camionRepositoryMock;
    @Autowired
    OrdenNegocio ordenNegocio;
    @Autowired
    CamionNegocio camionNegocio;

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
    public void setearFechaPesajeInicial() throws BadRequest, ConflictException, NoEncontradoException, NegocioException, UnprocessableException {
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
        assertThrows(UnprocessableException.class, () -> ordenNegocio.establecerPesajeInicial(orden));

        //System.out.println(ordenNegocio.establecerPesajeInicial(orden).getMensaje().getMensaje()); //Solo se puede pasar a estado 2 las ordenes en estado 1
    }

    @Test   //caso feliz
    public void frenarCarga() throws NoEncontradoException, NegocioException, UnprocessableException {
        double taraCamion = 5000;
        Date fechaPesoInicial = new Date();
        orden.setEstado(2);
        orden.setFechaPesajeInicial(fechaPesoInicial);
        orden.getCamion().setTara(taraCamion);
        orden.setMasaAcumuladaKg(1500);


        //carga
        carga = new Carga();
        carga.setMasaAcumuladaKg(/*(float) camion.getPreset()*/1500);
        carga.setDensidadProductoKilogramoMetroCub(454);
        carga.setTemperaturaProductoCelcius(25);
        carga.setCaudalLitroSegundo(3);
        carga.setFechaSalidaHW(new Date());
        carga.setOrden(orden);
        carga.setId(id);
        carga.setFechaEntradaBackEnd(new Date());

        camion.setPesoFinalCamion(camion.getPreset());

        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        Orden orden1 = ordenNegocio.frenarCargar(orden.getCodigoExterno()).getEntidad();
        //then
        assertEquals(3,orden1.getEstado());
        assertEquals(carga.getMasaAcumuladaKg(),orden1.getMasaAcumuladaKg());
       }

    @Test   //caso con estado incorrecto
    public void frenarCargaSinEstadoDos() {
        double taraCamion = 5000;
        Date fechaPesoInicial = new Date();
        orden.setEstado(3);
        orden.setFechaPesajeInicial(fechaPesoInicial);
        orden.getCamion().setTara(taraCamion);
        orden.setMasaAcumuladaKg(1500);


        //carga
        carga = new Carga();
        carga.setMasaAcumuladaKg(/*(float) camion.getPreset()*/1500);
        carga.setDensidadProductoKilogramoMetroCub(454);
        carga.setTemperaturaProductoCelcius(25);
        carga.setCaudalLitroSegundo(3);
        carga.setFechaSalidaHW(new Date());
        carga.setOrden(orden);
        carga.setId(id);
        carga.setFechaEntradaBackEnd(new Date());

        camion.setPesoFinalCamion(camion.getPreset());

        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        //then
        assertThrows(UnprocessableException.class, () -> ordenNegocio.frenarCargar(orden.getCodigoExterno()));
    }

    @Test   //caso con orden incorrecta
    public void frenarCargaConOrdenIncorrecta() {
        double taraCamion = 5000;
        Date fechaPesoInicial = new Date();
        orden.setEstado(3);
        orden.setFechaPesajeInicial(fechaPesoInicial);
        orden.getCamion().setTara(taraCamion);
        orden.setMasaAcumuladaKg(1500);


        //carga
        carga = new Carga();
        carga.setMasaAcumuladaKg(/*(float) camion.getPreset()*/1500);
        carga.setDensidadProductoKilogramoMetroCub(454);
        carga.setTemperaturaProductoCelcius(25);
        carga.setCaudalLitroSegundo(3);
        carga.setFechaSalidaHW(new Date());
        carga.setOrden(orden);
        carga.setId(id);
        carga.setFechaEntradaBackEnd(new Date());

        Orden ordenAux = new Orden();
        ordenAux.setCodigoExterno("5");

        camion.setPesoFinalCamion(camion.getPreset());

        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findByCodigoExterno(ordenAux.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.save(orden)).thenReturn(givenOrden.get());
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        //then
        assertThrows(NoEncontradoException.class, () -> ordenNegocio.frenarCargar(orden.getCodigoExterno()));
    }

    @Test   //caso feliz
    public void pesoFinal() throws UnprocessableException, NoEncontradoException, NegocioException {
        Date fechaPesoInicial = new Date();
        orden.setFechaPesajeInicial(fechaPesoInicial);
        orden.setFechaRecepcionPesajeFinal(fechaPesoInicial);
        orden.setMasaAcumuladaKg(1500);
        orden.setEstado(3);
        camion.setTara(1000);
        camion.setPesoFinalCamion(orden.getMasaAcumuladaKg()+ camion.getTara());

        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        Optional<Camion> givenCamion = Optional.of(camion);
        //when
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        when(camionRepositoryMock.findByPatente(camion.getPatente())).thenReturn(givenCamion);
        when(camionRepositoryMock.findById(camion.getId())).thenReturn(givenCamion);
        when(camionRepositoryMock.save(camion)).thenReturn(camion);
        when(ordenRepositoryMock.save(orden)).thenReturn(orden);
        //then

        Orden orden1 = ordenNegocio.establecerPesajeFinal(orden).getEntidad();

        assertEquals(4,orden1.getEstado());

    }

    @Test
    public void pesoFinalConEstadoIncorrecto() {
        Date fechaPesoInicial = new Date();
        orden.setFechaPesajeInicial(fechaPesoInicial);
        orden.setFechaRecepcionPesajeFinal(fechaPesoInicial);
        orden.setMasaAcumuladaKg(1500);
        orden.setEstado(4);
        camion.setTara(1000);
        camion.setPesoFinalCamion(orden.getMasaAcumuladaKg()+ camion.getTara());

        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        Optional<Camion> givenCamion = Optional.of(camion);
        //when
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        when(camionRepositoryMock.findByPatente(camion.getPatente())).thenReturn(givenCamion);
        when(camionRepositoryMock.findById(camion.getId())).thenReturn(givenCamion);
        when(camionRepositoryMock.save(camion)).thenReturn(camion);
        when(ordenRepositoryMock.save(orden)).thenReturn(orden);
        //then
        assertThrows(UnprocessableException.class, () -> ordenNegocio.establecerPesajeFinal(orden) );
    }

    @Test   //caso feliz
    public void conciliacion() throws UnprocessableException, NoEncontradoException, NegocioException {
        double taraCamion = 5000;
        Date fechaPesoInicial = new Date();
        orden.setEstado(4);
        orden.setFechaPesajeInicial(fechaPesoInicial);
        orden.getCamion().setTara(taraCamion);
        orden.setMasaAcumuladaKg(1500);
        camion.setTara(1000);
        camion.setPesoFinalCamion(3000);

        //carga
        carga = new Carga();
        carga.setMasaAcumuladaKg(1500);
        carga.setDensidadProductoKilogramoMetroCub(454);
        carga.setTemperaturaProductoCelcius(25);
        carga.setCaudalLitroSegundo(3);
        carga.setFechaSalidaHW(new Date());
        carga.setOrden(orden);
        carga.setId(id);
        carga.setFechaEntradaBackEnd(new Date());

        //conciliacionDTO
        ConciliacionDTO conciliacionDTO = new ConciliacionDTO(
                (float)camion.getTara(),
                (float)camion.getPesoFinalCamion(),
                carga.getMasaAcumuladaKg(),
                (float)(camion.getPesoFinalCamion() - camion.getTara()),
                (float)((camion.getPesoFinalCamion() - camion.getTara()) - carga.getMasaAcumuladaKg()),
                orden.getPromedDensidadProductoKilogramoMetroCub(),
                orden.getPromedioTemperaturaProductoCelcius(),
                orden.getPromedioCaudalLitroSegundo()
        );


        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.getPesoInicialAndPesoFinalAndMasaAcumuladaKgAndDiferenciaMasaAcu_DeltaPeso(orden.getId())).thenReturn(conciliacionDTO);
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        //then

        ConciliacionDTO conciliacion = ordenNegocio.obtenerConciliacion(orden.getCodigoExterno());
        assertEquals(conciliacionDTO,conciliacion);
    }

    @Test   //caso cuando no existe la orden
    public void conciliacionOrdenNoExistente(){
        double taraCamion = 5000;
        Date fechaPesoInicial = new Date();
        orden.setEstado(3);
        orden.setFechaPesajeInicial(fechaPesoInicial);
        orden.getCamion().setTara(taraCamion);
        orden.setMasaAcumuladaKg(1500);
        camion.setTara(1000);
        camion.setPesoFinalCamion(3000);

        //carga
        carga = new Carga();
        carga.setMasaAcumuladaKg(/*(float) camion.getPreset()*/1500);
        carga.setDensidadProductoKilogramoMetroCub(454);
        carga.setTemperaturaProductoCelcius(25);
        carga.setCaudalLitroSegundo(3);
        carga.setFechaSalidaHW(new Date());
        carga.setOrden(orden);
        carga.setId(id);
        carga.setFechaEntradaBackEnd(new Date());

        Orden ordenAux = new Orden();
        ordenAux.setCodigoExterno("5");

        //conciliacionDTO
        ConciliacionDTO conciliacionDTO = new ConciliacionDTO(
                (float)camion.getTara(),
                (float)camion.getPesoFinalCamion(),
                carga.getMasaAcumuladaKg(),
                (float)(camion.getPesoFinalCamion() - camion.getTara()),
                (float)((camion.getPesoFinalCamion() - camion.getTara()) - carga.getMasaAcumuladaKg()),
                orden.getPromedDensidadProductoKilogramoMetroCub(),
                orden.getPromedioTemperaturaProductoCelcius(),
                orden.getPromedioCaudalLitroSegundo()
        );


        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findByCodigoExterno(ordenAux.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.getPesoInicialAndPesoFinalAndMasaAcumuladaKgAndDiferenciaMasaAcu_DeltaPeso(orden.getId())).thenReturn(conciliacionDTO);
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        //then

        assertThrows(NoEncontradoException.class, () -> ordenNegocio.obtenerConciliacion(orden.getCodigoExterno()));
    }

    @Test   //caso cuando el estado ni 3 ni 4
    public void conciliacionEstadoIncorrecto() {
        double taraCamion = 5000;
        Date fechaPesoInicial = new Date();
        orden.setEstado(2);
        orden.setFechaPesajeInicial(fechaPesoInicial);
        orden.getCamion().setTara(taraCamion);
        orden.setMasaAcumuladaKg(1500);
        camion.setTara(1000);
        camion.setPesoFinalCamion(3000);

        //carga
        carga = new Carga();
        carga.setMasaAcumuladaKg(/*(float) camion.getPreset()*/1500);
        carga.setDensidadProductoKilogramoMetroCub(454);
        carga.setTemperaturaProductoCelcius(25);
        carga.setCaudalLitroSegundo(3);
        carga.setFechaSalidaHW(new Date());
        carga.setOrden(orden);
        carga.setId(id);
        carga.setFechaEntradaBackEnd(new Date());

        //conciliacionDTO
        ConciliacionDTO conciliacionDTO = new ConciliacionDTO(
                (float)camion.getTara(),
                (float)camion.getPesoFinalCamion(),
                carga.getMasaAcumuladaKg(),
                (float)(camion.getPesoFinalCamion() - camion.getTara()),
                (float)((camion.getPesoFinalCamion() - camion.getTara()) - carga.getMasaAcumuladaKg()),
                orden.getPromedDensidadProductoKilogramoMetroCub(),
                orden.getPromedioTemperaturaProductoCelcius(),
                orden.getPromedioCaudalLitroSegundo()
        );


        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findByCodigoExterno(orden.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.getPesoInicialAndPesoFinalAndMasaAcumuladaKgAndDiferenciaMasaAcu_DeltaPeso(orden.getId())).thenReturn(conciliacionDTO);
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        //then

        assertThrows(UnprocessableException.class, () -> ordenNegocio.obtenerConciliacion(orden.getCodigoExterno()));
    }
}