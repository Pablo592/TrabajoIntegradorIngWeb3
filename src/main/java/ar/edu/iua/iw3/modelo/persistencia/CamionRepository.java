package ar.edu.iua.iw3.modelo.persistencia;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ar.edu.iua.iw3.modelo.Camion;

@Repository
public interface CamionRepository  extends JpaRepository<Camion, Long> {

	Optional<Camion> findByPatente(String patente);

}
