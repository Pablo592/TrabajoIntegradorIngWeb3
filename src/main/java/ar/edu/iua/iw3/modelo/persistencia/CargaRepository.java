package ar.edu.iua.iw3.modelo.persistencia;

import ar.edu.iua.iw3.modelo.Carga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CargaRepository extends JpaRepository<Carga, Long> {
}