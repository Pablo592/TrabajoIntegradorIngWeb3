package ar.edu.iua.iw3.eventos;

import org.springframework.context.ApplicationEvent;

public class CargaEvent extends ApplicationEvent{

    public enum Tipo { SUPERADO_UMBRAL_DE_TEMPERATURA}

    private Tipo tipo;

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public CargaEvent(Object source) {
        super(source);
    }

    public CargaEvent(Object source,Tipo tipo) {
        super(source);
        this.tipo = tipo;
    }
}
