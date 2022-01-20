package ar.edu.iua.iw3.modelo.persistencia;

import ar.edu.iua.iw3.modelo.Carga;
import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.modelo.Producto;
import ar.edu.iua.iw3.modelo.dto.CargaDTO;
import ar.edu.iua.iw3.modelo.dto.ConciliacionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    Optional<Orden> findByCodigoExterno(String numeroOrden);

    @Query(nativeQuery = true)
    ConciliacionDTO getPesoInicialAndPesoFinalAndMasaAcumuladaKgAndDiferenciaMasaAcu_DeltaPeso(long orden_id);
}

