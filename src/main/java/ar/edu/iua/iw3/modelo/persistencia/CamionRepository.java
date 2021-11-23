package ar.edu.iua.iw3.modelo.persistencia;

import java.util.Optional;

import ar.edu.iua.iw3.modelo.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CamionRepository  extends JpaRepository<Camion, Long> {

	Optional<Camion> findByPatente(String patente);
}
