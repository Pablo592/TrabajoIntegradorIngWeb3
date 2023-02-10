package ar.edu.iua.iw3.modelo.persistencia;

import ar.edu.iua.iw3.modelo.Carga;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.modelo.Producto;
import ar.edu.iua.iw3.modelo.dto.CargaDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.lang.annotation.Native;
import java.util.List;

@Repository
public interface CargaRepository extends JpaRepository<Carga, Long> {

    @Query(value = "select avg (caudal_litro_segundo) from carga where  orden_id=:id", nativeQuery = true)
    public Carga getPromedioCaudal(@Param("id") long id);

    @Query(nativeQuery = true)
    CargaDTO getPromedioDensidadAndTemperaturaAndCaudal(long orden_id);

    @Query(value ="SELECT * FROM carga where orden_id =:id order by id desc limit 1", nativeQuery = true)
    public Carga findTheLastCarga(@Param("id") long id);

}
