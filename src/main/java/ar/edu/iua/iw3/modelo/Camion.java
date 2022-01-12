package ar.edu.iua.iw3.modelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.List;
import javax.persistence.*;

@ApiModel(description = "Esta clase representa el camión que se abastecerá de combustible.")
@Entity
@Table(name = "camion")
public class Camion  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ApiModelProperty(notes = "Patente del camión.", example = "AB123CD", required = true)
    @Column(length = 7, nullable = false, unique = true)
    private String patente;

    @ApiModelProperty(notes = "Descripción del camión.", example = "Camión Cisterna")
    @Column(length = 100)
    private String descripcion;

    @ApiModelProperty(notes = "Capacidad de almacenamiento de combustible (litros).", example = "150", required = true)
    @Column(columnDefinition = "DOUBLE", nullable = false)
    private double cisternadoLitros;

    @ApiModelProperty(notes = "Peso máximo de carga soportado por el camión (kg).", example = "2000", required = true)
    @Column(columnDefinition = "DOUBLE", nullable = false)
    private double preset;              //Limite a cargar en el camion

    @ApiModelProperty(notes = "Peso del camión sin carga (kg).", example = "4000")
    @Column(columnDefinition = "DOUBLE default 0")
    private double tara;                //Peso del camion vacio

    @ApiModelProperty(notes = "Peso del camión vacío mas el peso de la carga (kg).", example = "6000")
    @Column(columnDefinition = "DOUBLE default 0")
    private double pesoFinalCamion;		//Peso del camion tras cargarse

    @ApiModelProperty(notes = "El camión puede generar varias ordenes al abastecerse continuamente.")
    @OneToMany(targetEntity = Orden.class, mappedBy = "camion", fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Orden> ordenList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCisternadoLitros() {
        return cisternadoLitros;
    }

    public void setCisternadoLitros(double cisternadoLitros) {
        this.cisternadoLitros = cisternadoLitros;
    }

    public double getPreset() {
        return preset;
    }

    public void setPreset(double preset) {
        this.preset = preset;
    }

    public double getTara() {
        return tara;
    }

    public void setTara(double tara) {
        this.tara = tara;
    }

    public double getPesoFinalCamion() {
        return pesoFinalCamion;
    }

    public void setPesoFinalCamion(double pesoFinalCamion) {
        this.pesoFinalCamion = pesoFinalCamion;
    }

    public List<Orden> getOrdenList() {
        return ordenList;
    }

    public void setOrdenList(List<Orden> ordenList) {
        this.ordenList = ordenList;
    }

    public boolean checkPatente(String patente){
        String primeraParte = patente.substring(0,2).toUpperCase();
        String segundaParte = patente.substring(2,5);
        String terceraParte = patente.substring(5).toUpperCase();
        if(isContainsLetras(primeraParte) && isContainsNumeros(segundaParte) && isContainsLetras(terceraParte))
            return true;
        return false;
    }
    private boolean isContainsLetras(String palabra){
        for(int i = 0;i<palabra.length();i++){
            int codigoAscii=palabra.codePointAt(i);
            if(65>codigoAscii || 90<codigoAscii)
                return false;
        }
        return true;
    }

    private boolean isContainsNumeros(String palabra){
        for(int i = 0;i<palabra.length();i++){
            int codigoAscii=palabra.codePointAt(i);
            if(48>codigoAscii || 57<codigoAscii)
                return false;
        }
        return true;
    }

    public String checkBasicData(){
        if(getPatente().trim().length()==0)
            return "El atributo 'patente' es obligatorio";
        if(!checkPatente(getPatente()))
            return "El atributo 'patente' tiene un mal formato";
        if(getCisternadoLitros()<=0)
            return "El atributo 'cisternadoLitros' tiene que ser mayor a cero";
        if(getPreset()<=0)
            return "El atributo 'preset' tiene que ser mayor a cero";
    return null;
    }
}
