package ar.edu.iua.iw3.modelo.persistencia;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.iua.iw3.modelo.Chofer;


@Repository
public interface ChoferRepository extends JpaRepository<Chofer, Long> {

	Optional<Chofer> findByDocumento(long documento);


}
