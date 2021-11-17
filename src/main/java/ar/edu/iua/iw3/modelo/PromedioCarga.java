package ar.edu.iua.iw3.modelo;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "PromedioCarga")
public class PromedioCarga extends Carga implements Serializable {
    private static final long serialVersionUID = -3470409984466313024L;
    private int cantidadCarga = 0;
    private double acumuladorDensidad = 0;
    private double acumuladorTemperatura = 0;
    private double acumuladorCaudal = 0;


    public PromedioCarga() {}

    public PromedioCarga(float masaAcumuladaKg, float densidadProductoKilogramoMetroCub, float temperaturaProductoCelcius, float caudalLitroSegundo) {
        super(masaAcumuladaKg, densidadProductoKilogramoMetroCub, temperaturaProductoCelcius, caudalLitroSegundo);
    }
}
