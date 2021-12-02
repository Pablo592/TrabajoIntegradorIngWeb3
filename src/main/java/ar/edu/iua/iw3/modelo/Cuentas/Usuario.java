package ar.edu.iua.iw3.modelo.Cuentas;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name="usuarios")
public class Usuario implements UserDetails, Serializable {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(length = 70, nullable = false)
	private String nombre;
	@Column(length = 70, nullable = false)
	private String apellido;
	@Column(length = 200, nullable = true, unique = true)
	private String email;

	@Column(length = 150, nullable = true)
	private String password;
	
	@Column(length = 50, unique=true, nullable = false)
	private String username;
	
	@Column(columnDefinition = "TINYINT DEFAULT 1")
	private boolean accountNonExpired;
	
	@Column(columnDefinition = "TINYINT DEFAULT 1")
	private boolean accountNonLocked;
	
	@Column(columnDefinition = "TINYINT DEFAULT 1")
	private boolean credentialsNonExpired;

	@Column(columnDefinition = "TINYINT DEFAULT 1")
	private boolean enabled;
	
	private static final long serialVersionUID = -1920138525852395737L;

	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		if(accountNonLocked)
			setIntentosFallidos(0);
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usuarios_roles",
		joinColumns = {@JoinColumn(name="id_usuario", referencedColumnName = "id")},
		inverseJoinColumns = {@JoinColumn(name="id_rol", referencedColumnName = "id")}
	)
	private Set<Rol> roles;
	
	public Set<Rol> getRoles() {
		return roles;
	}

	public void setRoles(Set<Rol> roles) {
		this.roles = roles;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
//		List<GrantedAuthority> roles=new ArrayList<GrantedAuthority>();
//		for(Rol r: getRoles()) {
//			roles.add(new SimpleGrantedAuthority(r.getNombre()));
//		}
		List<GrantedAuthority> roles=getRoles()
				.stream()
				.map(r -> new SimpleGrantedAuthority(r.getNombre()))
				.collect(Collectors.toList());
		return roles;
	}
	
	/*
	 * - r1                                                              - SGA(r1)
	 * - r2     r1 r2 r3 r4  map                                         - SGA(r2)
	 * - r3 =>  -  -  -  -  ====>  SGA(r1) SGA(r12) SGA(r3) SGA(r4) =>   - SGA(r3)
	 * - r4                                                              - SGA(r4)
	 */
	
	
	public String checkAccount(PasswordEncoder passwordEncoder, String password) {
		
		if (!isEnabled())
			return "ACCOUNT_NOT_ENABLED";
		if (!isAccountNonLocked())
			return "ACCOUNT_LOCKED";
		if (!isCredentialsNonExpired())
			return "CREDENTIALS_EXPIRED";
		if (!isAccountNonExpired())
			return "ACCOUNT_EXPIRED";
		if (!passwordEncoder.matches(password, getPassword()))
			return "BAD_PASSWORD";
		return null;
	}
	
	@Transient
	public String getNombreCompleto() {
		return String.format("%s, %s", getApellido(), getNombre());
	}
	
	private int duracionToken;
	private int intentosFallidos;
	
	private static int MAXIMO_INTENTOS_FALLIDOS=3;
	public void agregaIntentoFallido() {
		intentosFallidos++;
		if(intentosFallidos>=MAXIMO_INTENTOS_FALLIDOS) {
			setIntentosFallidos(0);
			setAccountNonLocked(false);
		}
	}



	public int getDuracionToken() {
		return duracionToken;
	}

	public void setDuracionToken(int duracionToken) {
		this.duracionToken = duracionToken;
	}

	public int getIntentosFallidos() {
		return intentosFallidos;
	}

	public void setIntentosFallidos(int intentosFallidos) {
		this.intentosFallidos = intentosFallidos;
	}
	
	

}
