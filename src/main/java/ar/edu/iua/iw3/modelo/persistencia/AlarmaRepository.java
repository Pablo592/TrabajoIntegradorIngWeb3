package ar.edu.iua.iw3.modelo.persistencia;

import ar.edu.iua.iw3.modelo.Alarma;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmaRepository extends JpaRepository<Alarma, Long> {
}