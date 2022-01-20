package ar.edu.iua.iw3.auth;

import ar.edu.iua.iw3.modelo.Cuentas.IUsuarioNegocio;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.negocio.excepciones.NegocioException;
import ar.edu.iua.iw3.negocio.excepciones.NoEncontradoException;
import ar.edu.iua.iw3.security.authtoken.AuthToken;
import ar.edu.iua.iw3.security.authtoken.IAuthTokenBusiness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

//Sección Test del login-json de Postman
//var jsonData = pm.response.json();
//pm.globals.set("tokenIW3", jsonData.authtoken);
public class CustomTokenAuthenticationFilter extends OncePerRequestFilter {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	
	public CustomTokenAuthenticationFilter(IAuthTokenBusiness authTokenBusiness, IUsuarioNegocio userBusiness) {
		super();
		this.authTokenBusiness = authTokenBusiness;
		this.userBusiness = userBusiness;
	}

	private IAuthTokenBusiness authTokenBusiness;

	private IUsuarioNegocio userBusiness;

	public static String ORIGIN_TOKEN_TOKEN = "token";
	public static String ORIGIN_TOKEN_HEADER = "header";

	public static String AUTH_HEADER = "X-AUTH-TOKEN";
	public static String AUTH_HEADER1 = "XAUTHTOKEN";
	public static String AUTH_PARAMETER = "xauthtoken";
	public static String AUTH_PARAMETER1 = "token";
	
	public static String AUTH_PARAMETER_AUTHORIZATION = "Authorization";


	private boolean esValido(String valor) {
		return valor != null && valor.trim().length() > 10;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String parameter = request.getParameter(AUTH_PARAMETER);
		if (!esValido(parameter)) {
			parameter = request.getParameter(AUTH_PARAMETER1);
		}
		String header = request.getHeader(AUTH_HEADER);
		if (!esValido(header)) {
			header = request.getHeader(AUTH_PARAMETER_AUTHORIZATION);
			if(header!=null && header.toLowerCase().startsWith("bearer ")) {
				header=header.substring("Bearer ".length());
			} 
		}
		if (!esValido(parameter) && !esValido(header)) {
			chain.doFilter(request, response);
			return;
		}
		String token = "";
		if (esValido(parameter)) {
			token = parameter;
			log.trace("Token recibido por query param=" + token);
		} else {
			token = header;
			log.trace("Token recibido por header=" + token);
		}
		String[] tokens = null;
		AuthToken authToken = null;

		try {
			tokens = AuthToken.decode(token);
			if (tokens.length != 2) {
				chain.doFilter(request, response);
				return;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			chain.doFilter(request, response);
			return;
		}

		// A partir de aquí, se considera que se envió el token, por
		// ende si no está ok, login inválido

		try {
			authToken = authTokenBusiness.load(tokens[0]);
		} catch (NoEncontradoException e) {
			SecurityContextHolder.clearContext();
			log.debug("No existe el token=" + token);
			chain.doFilter(request, response);
			return;
		} catch (NegocioException e) {
			SecurityContextHolder.clearContext();
			log.error(e.getMessage(), e);
			chain.doFilter(request, response);
			return;
		}

		if (!authToken.valid()) {
			try {
				if (authToken.getType().equals(AuthToken.TYPE_DEFAULT)
						|| authToken.getType().equals(AuthToken.TYPE_TO_DATE)
						|| authToken.getType().equals(AuthToken.TYPE_REQUEST_LIMIT)) {
					authTokenBusiness.delete(authToken);
				}
				if (authToken.getType().equals(AuthToken.TYPE_FROM_TO_DATE)) {
					//Solo se chequea si se comenzó el período, porque puede ser a futuro
					if (authToken.getTo().getTime() < System.currentTimeMillis()) {
						authTokenBusiness.delete(authToken);
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
			SecurityContextHolder.clearContext();
			log.debug("El Token " + token + " ha expirado");
			chain.doFilter(request, response);
			return;
		}

		try {
			authToken.setLast_used(new Date());
			authToken.addRequest();
			authTokenBusiness.save(authToken);
			String username = authToken.getUsername();
			Usuario u = null;
			try {
				u = userBusiness.cargarPorNombreOEmail(username);
				// u.setSessionToken(token);
				log.trace("Token para usuario {} ({}) [{}]", u.getUsername(), token, request.getRequestURI());
				UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(u, null,
						u.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(auth);
			} catch (NoEncontradoException e) {
				log.debug("No se encontró el usuario {} por token", username);
			}
			chain.doFilter(request, response);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			chain.doFilter(request, response);
		}

	}
}