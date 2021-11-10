package ar.edu.iua.iw3;

import ar.edu.iua.iw3.modelo.Producto;
import ar.edu.iua.iw3.negocio.ClienteNegocio;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("mysqldev")
@RunWith(SpringRunner.class)
@SpringBootTest
public class ClienteNegocioTest {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();


    @Autowired
    ClienteNegocio clienteNegocio;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static String description = "Lomito2";
    private static Producto prod1;


    //
    @Test(expected = NoEncontradoException.class)
    public void testGetClientNotExist() throws NegocioException, NoEncontradoException {
        int id = 2;
        clienteNegocio.cargar(id).getRazonSocial();
        expectedEx.expect(NoEncontradoException.class);
    }

    @Test
    public void testGetRazonSocialSucess() throws NegocioException, NoEncontradoException {
        int id = 1;
        assertEquals("SA", clienteNegocio.cargar(id).getRazonSocial());
    }
}
