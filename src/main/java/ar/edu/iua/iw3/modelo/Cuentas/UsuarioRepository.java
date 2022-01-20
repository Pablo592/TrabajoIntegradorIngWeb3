package ar.edu.iua.iw3.modelo.Cuentas;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

	public Optional<Usuario> findFirstByUsernameOrEmail(String username, String email);

	public Optional<Usuario> findFirstByEmail(String email);

	public Optional<Usuario> findFirstByUsername(String username);
}
