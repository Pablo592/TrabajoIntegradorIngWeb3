package ar.edu.iua.iw3.modelo.persistencia;

import ar.edu.iua.iw3.modelo.Alarma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmaRepository extends JpaRepository<Alarma, Long> {
    Optional<List<Alarma>> findAllByAutor_Id(int id);
}