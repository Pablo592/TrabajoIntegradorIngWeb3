package ar.edu.iua.iw3.modelo.persistencia;

import ar.edu.iua.iw3.modelo.Orden;
import ar.edu.iua.iw3.modelo.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
    Optional<Orden> findByNumeroDeOrden(long numeroOrden);
}