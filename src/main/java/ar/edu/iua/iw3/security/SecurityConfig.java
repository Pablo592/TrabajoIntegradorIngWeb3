package ar.edu.iua.iw3.security;

import ar.edu.iua.iw3.auth.CustomTokenAuthenticationFilter;
import ar.edu.iua.iw3.modelo.Cuentas.IUsuarioNegocio;
import ar.edu.iua.iw3.security.authtoken.IAuthTokenBusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private IAuthTokenBusiness authTokenBusiness;

	@Autowired
	private IUsuarioNegocio userBusiness;

	@Autowired
	private UserDetailsService userDetailService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailService);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(Arrays.asList("Access-Control-Allow-Headers", "Access-Control-Allow-Origin",
				"Access-Control-Request-Method", "Access-Control-Request-Headers", "Origin", "Cache-Control",
				"Content-Type", "Authorization", "XAUTHTOKEN", "X-Requested-With", "X-AUTH-TOKEN"));

		configuration.setAllowedMethods(Arrays.asList("DELETE", "GET", "POST", "PATCH", "PUT", "OPTIONS"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();

		http.cors();

		http.authorizeRequests().antMatchers("/v2/api-docs").permitAll();
		http.authorizeRequests().antMatchers("/configuration/ui").permitAll();
		http.authorizeRequests().antMatchers("/swagger-resources/**").permitAll();
		http.authorizeRequests().antMatchers("/configuration/security").permitAll();
		http.authorizeRequests().antMatchers("/swagger-ui.html").permitAll();
		http.authorizeRequests().antMatchers("/swagger-ui/*").permitAll();
		http.authorizeRequests().antMatchers("/webjars/**").permitAll();


		//autorizo a todos a consumir este archivo que esta fuera de la carpeta y sus imagenes
		http.authorizeRequests().antMatchers("/index.html").permitAll();
		http.authorizeRequests().antMatchers("/sisterna.png").permitAll();
		http.authorizeRequests().antMatchers("/campana*").permitAll();
		http.authorizeRequests().antMatchers("/cerrarSesion.png").permitAll();
		http.authorizeRequests().antMatchers("/fechaAbajo.png").permitAll();
		//autorizo para que se acceda a este end-point para facilitar desde el front  evitar loguearme
		http.authorizeRequests().antMatchers("/produccion/api/v1/ordenes").permitAll();


		//autorizo a que se accedan a los archivos que estan en los directorios hijos de ui
		http.authorizeRequests().antMatchers("/ui/**").permitAll();
		http.authorizeRequests().antMatchers("/").permitAll();

		//autorizo para que se acceda a este end-point para facilitar desde el front  evitar loguearme
		//http.authorizeRequests().antMatchers("/test/api/v1/ordenes*").permitAll();
		// //.hasRole("ADMIN");
		//http.authorizeRequests().antMatchers("/test*").hasAnyRole("ADMIN", "USER");

		http.authorizeRequests().antMatchers("/produccion/api/v1/auth/login*").permitAll();


		http.authorizeRequests().anyRequest().authenticated();

		http.addFilterAfter(new CustomTokenAuthenticationFilter(authTokenBusiness, userBusiness),
				UsernamePasswordAuthenticationFilter.class);

		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

}