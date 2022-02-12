package ar.edu.iua.iw3.eventos;

import ar.edu.iua.iw3.modelo.Carga;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.negocio.OrdenNegocio;
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

    @Value("${mail.carga.umbralTemperatura.to:pgaido524@alumnos.iua.edu.ar}")
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

        if(carga.getOrden().isAlarmaActiva()){
            System.out.println("como la alarma esta activada no se puede enviar el mail hasta que lo acepte");
            return;
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
            ordenConMailEnviado.setAlarmaActiva(true);
            ordenNegocio.modificar(ordenConMailEnviado);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

}
