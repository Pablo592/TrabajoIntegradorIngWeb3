package ar.edu.iua.iw3;

import ar.edu.iua.iw3.util.Constantes;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class webSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/iw3");
        registry.setApplicationDestinationPrefixes("/wsin");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
       registry.addEndpoint(Constantes.URL_SOCKET).setAllowedOrigins("*").withSockJS();
    }
}