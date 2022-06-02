package ar.edu.iua.iw3.eventos;

import ar.edu.iua.iw3.modelo.Carga;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.negocio.OrdenNegocio;
import ar.edu.iua.iw3.negocio.excepciones.ConflictException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class CargaEventListener implements ApplicationListener<CargaEvent> {

    private Logger log = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private OrdenNegocio ordenNegocio;

    @Value("${mail.carga.umbralTemperatura.to}")
    private String to;

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void onApplicationEvent(CargaEvent event) {
        if(event.getTipo().equals(CargaEvent.Tipo.SUPERADO_UMBRAL_DE_TEMPERATURA) && event.getSource() instanceof Carga){
            manejaEventoSuperadoUmbralTemperatura((Carga) event.getSource());
        }
    }



    private void manejaEventoSuperadoUmbralTemperatura(Carga carga){
        String mensaje = String.format("El combustible abastecido en la orden %s, supero el umbral de temperatura al tener %.2f Cª"
                ,carga.getOrden().getCodigoExterno(),carga.getTemperaturaProductoCelcius());

        /*
        t           t+1
        False --> true se activa por primera vez
        False --> False no se genera alarma
        True  --> True no se envia alarma
        True  --> False --> Acepta la alarma el usuario
        * */
        if(carga.getOrden().isEnviarMailActivo()){
            System.out.println("como la alarma esta activada no se puede enviar el mail hasta que lo acepte");
            return;
        }else {
            try {
                ordenNegocio.modificar(carga.getOrden()).setEnviarMailActivo(true);
            } catch (NegocioException e) {
                throw new RuntimeException(e);
            } catch (NoEncontradoException e) {
                throw new RuntimeException(e);
            } catch (ConflictException e) {
                throw new RuntimeException(e);
            }
        }

        log.info("Enviando mensaje '{}'",mensaje);

        try {
            SimpleMailMessage message =new SimpleMailMessage();
            message.setFrom("spitalevictor@gmail.com");
            message.setTo(to);
            message.setSubject("Precaucion Altas temperaturas en el combustible de la orden " + carga.getOrden().getCodigoExterno());
            message.setText(mensaje);
            emailSender.send(message);
            log.trace("Mail enviado a: '{}'",to);


            Orden ordenConMailEnviado = carga.getOrden();
            ordenConMailEnviado.setEnviarMailActivo(true);
            ordenNegocio.modificar(ordenConMailEnviado);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

}
