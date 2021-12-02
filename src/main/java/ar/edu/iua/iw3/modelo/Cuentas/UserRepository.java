package ar.edu.iua.iw3.modelo.Cuentas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Usuario, Integer> {

	public Optional<Usuario> findFirstByUsernameOrEmail(String username, String email);
	
}
