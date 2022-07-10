package ar.edu.iua.iw3.negocio;

import ar.edu.iua.iw3.modelo.*;
import ar.edu.iua.iw3.modelo.dto.ConciliacionDTO;
import ar.edu.iua.iw3.modelo.persistencia.CamionRepository;
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
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
@RunWith(SpringRunner.class)
@SpringBootTest
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
    public void buscarOrdenPorCodigoExterno_Success() {
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
    public void buscarOrdenPorId_Success() throws NoEncontradoException, NegocioException {
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
    @Test
    public void crearOrdenConMetadatosSuficientes_Success() throws EncontradoException, BadRequest, NegocioException, UnprocessableException {
        //when + given
        when(ordenRepositoryMock.save(orden)).thenReturn(orden);
        Orden ordenCreada = ordenNegocio.agregar(orden).getEntidad();
        //then
        assertEquals(ordenCreada.getCodigoExterno(),orden.getCodigoExterno());
        assertEquals(1,ordenCreada.getEstado());
    }
    @Test
    public void crearOrdenSinCamionAsociado_BadRequest() {
        orden.setCamion(new Camion());
        //when + given + then
        when(ordenRepositoryMock.save(orden)).thenReturn(orden);
        assertThrows(BadRequest.class, () -> ordenNegocio.agregar(orden));
    }
    @Test
    public void crearOrdenSinClienteAsociado_BadRequest() {
        orden.setCliente(new Cliente());
        //when + given + then
        when(ordenRepositoryMock.save(orden)).thenReturn(orden);
        assertThrows(BadRequest.class, () -> ordenNegocio.agregar(orden));
    }
    @Test
    public void crearOrdenSinChoferAsociado_BadRequest(){
        orden.setChofer(new Chofer());
        //when + given + then
        when(ordenRepositoryMock.save(orden)).thenReturn(orden);
        assertThrows(BadRequest.class, () -> ordenNegocio.agregar(orden));
    }
    @Test
    public void crearOrdenSinProductoAsociado_BadRequest() {
        orden.setProducto(new Producto());
        //when + given + then
        when(ordenRepositoryMock.save(orden)).thenReturn(orden);
        assertThrows(BadRequest.class, () -> ordenNegocio.agregar(orden));
    }
    @Test
    public void setearFechaPesajeInicial_Success() throws BadRequest, ConflictException, NoEncontradoException, NegocioException, UnprocessableException {
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
    public void establecerPesajeInicialSinTara_BadRequest()  {
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
    }
    @Test
    public void establecerPesajeInicialSinFechaPesajeInicial_BadRequest() {
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
    }
    @Test
    public void establecerPesajeInicialSinPatente_BadRequest() {
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
    }
    @Test
    public void establecerPesajeInicialConEstadoOrdenDiferenteDe1_UnprocessableException()  {
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
    }
    @Test
    public void frenarCarga_Success() throws NoEncontradoException, NegocioException, UnprocessableException {
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
    @Test
    public void frenarCargaConEstadoIncorrecto_UnprocessableException() {
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
    @Test
    public void frenarCargaConOrdenIncorrecta_NoEncontradoException() {
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
    @Test
    public void pesoFinal_Sucess() throws UnprocessableException, NoEncontradoException, NegocioException {
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
    public void pesoFinalConEstadoIncorrecto_UnprocessableException() {
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
    @Test
    public void conciliacion_Sucess() throws UnprocessableException, NoEncontradoException, NegocioException {
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
    @Test
    public void conciliacionOrdenNoExistente_NoEncontradoException(){
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
                orden.getPromedioCaudalLitroSegundo());
        //given
        Optional<Orden> givenOrden = Optional.of(orden);
        //when
        when(ordenRepositoryMock.findByCodigoExterno(ordenAux.getCodigoExterno())).thenReturn(givenOrden);
        when(ordenRepositoryMock.getPesoInicialAndPesoFinalAndMasaAcumuladaKgAndDiferenciaMasaAcu_DeltaPeso(orden.getId())).thenReturn(conciliacionDTO);
        when(ordenRepositoryMock.findById(orden.getId())).thenReturn(givenOrden);
        //then
        assertThrows(NoEncontradoException.class, () -> ordenNegocio.obtenerConciliacion(orden.getCodigoExterno()));
    }
    @Test
    public void conciliacionEstadoIncorrecto_UnprocessableException() {
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
                orden.getPromedioCaudalLitroSegundo());
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