package ar.edu.iua.iw3.eventos;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

@Configuration
public class AsyncAppEventConfig {

    @Bean
    public ApplicationEventMulticaster configMulticaster(){
        SimpleApplicationEventMulticaster r = new SimpleApplicationEventMulticaster();
        r.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return r;
    }

}
