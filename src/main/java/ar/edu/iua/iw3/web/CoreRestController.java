package ar.edu.iua.iw3.web;

import ar.edu.iua.iw3.modelo.Cuentas.IUsuarioNegocio;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.negocio.excepciones.EncontradoException;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CoreRestController extends BaseRestController{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IUsuarioNegocio userBusiness;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	//https://www.iua.edu.ar/?username=pepe&password=123
	//
	
	@PostMapping(value="/login")
	public ResponseEntity<String> loginToken(@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password) {
		try {
			Usuario u = userBusiness.cargarPorNombreOEmail(username);
			String msg = u.checkAccount(passwordEncoder, password);
			if (msg != null) {
				u.agregaIntentoFallido();
				try {
					userBusiness.modificar(u);
				} catch (EncontradoException e) {
					log.error(e.getMessage());
					return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
				return new ResponseEntity<String>(msg, HttpStatus.UNAUTHORIZED);
			} else {
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, null,
						u.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);
				return new ResponseEntity<String>(userToJson(getUserLogged()).get("authtoken").toString(),
						HttpStatus.OK);
			}
		} catch (NegocioException e) {
			log.error(e.getMessage());
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NoEncontradoException e) {
			return new ResponseEntity<String>("BAD_ACCOUNT_NAME", HttpStatus.UNAUTHORIZED);
		}

	}
	
	@PostMapping(value="/login-json")
	public ResponseEntity<String> loginTokenFullJson(@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password) {
		try {
			Usuario u = userBusiness.cargarPorNombreOEmail(username);
			String msg = u.checkAccount(passwordEncoder, password);
			if (msg != null) {
				u.agregaIntentoFallido();
				try {
					userBusiness.modificar(u);
				} catch (EncontradoException e) {
					log.error(e.getMessage());
					return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
				}
				return new ResponseEntity<String>(msg, HttpStatus.UNAUTHORIZED);
			} else {
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, null,
						u.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);
				return new ResponseEntity<String>(userToJson(getUserLogged()).toString(),
						HttpStatus.OK);
			}
		} catch (NegocioException e) {
			log.error(e.getMessage());
			return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (NoEncontradoException e) {
			return new ResponseEntity<String>("BAD_ACCOUNT_NAME", HttpStatus.UNAUTHORIZED);
		}

	}
	
	@GetMapping(value = "/auth-info")
	public ResponseEntity<String> authInfo() {
		return new ResponseEntity<String>(userToJson(getUserLogged()).toString(), HttpStatus.OK);
	}

}
