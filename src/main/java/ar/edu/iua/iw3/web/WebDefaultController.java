package ar.edu.iua.iw3.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebDefaultController { //lo utilizo para hacer mapeo a un recurso estatico

    @GetMapping(value = "/")
    public String defaultPage(){
        return "redirect:/index.html";  //devuelvo una directiva http, que es un status code = 302 + una cabecera location de http
    }
}
