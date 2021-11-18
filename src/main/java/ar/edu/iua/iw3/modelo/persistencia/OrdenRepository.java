package ar.edu.iua.iw3.modelo.persistencia;

import ar.edu.iua.iw3.modelo.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    Optional<Orden> findByCodigoExterno(String numeroOrden);
}