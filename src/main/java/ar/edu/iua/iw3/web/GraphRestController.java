package ar.edu.iua.iw3.web;

import ar.edu.iua.iw3.negocio.IGraphNegocio;
import ar.edu.iua.iw3.util.Constantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constantes.URL_SOCKET_GRAFICOS)
public class GraphRestController {

    @Autowired
    private IGraphNegocio graphService;

    @GetMapping("/push")
    public void push() {
        System.out.print("HOLAAAAA EN EL SOCKET");
     //   graphService.pushGraphData();
    }

}