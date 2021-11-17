package ar.edu.iua.iw3.modelo;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name ="UltimaCarga" )
public class UltimaCarga extends Carga implements Serializable {
    private static final long serialVersionUID = -3470409984466313024L;

    public UltimaCarga() {
    }

    public UltimaCarga(float masaAcumuladaKg, float densidadProductoKilogramoMetroCub, float temperaturaProductoCelcius, float caudalLitroSegundo) {
        super(masaAcumuladaKg, densidadProductoKilogramoMetroCub, temperaturaProductoCelcius, caudalLitroSegundo);
    }
}
