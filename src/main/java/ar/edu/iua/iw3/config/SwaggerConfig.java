package ar.edu.iua.iw3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

import static javassist.CtClass.version;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInfo());
    }
    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "Sistema gestor de cargas de liquidos ",
                "Se desarrollo un sistema que permita interactuar con sistemas externos tiene el objetivo de cargar correctamente las cisternas de los camiones, cumpliendo los requerimientos establecidos por el cliente",
                "2.0",
                "http://codmind.com/terms",
                new Contact("Pablo Gaido && Joel Spitale", "https://github.com/Pablo592", "pgaido524@alumnos.iua.edu.ar"),
                "LICENSE",
                "LICENSE URL",
                Collections.emptyList()
        );
    }
}