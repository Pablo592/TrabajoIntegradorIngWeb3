package ar.edu.iua.iw3.util;

import ar.edu.iua.iw3.modelo.Cuentas.Rol;
import ar.edu.iua.iw3.modelo.Cuentas.Usuario;
import ar.edu.iua.iw3.security.authtoken.AuthToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.HashSet;
import java.util.Set;

public class Utilidades {
    public String getToken() {
        Rol admin = new Rol(1, "ROLE_USER", "Testing del sistema");
        Set<Rol> roles = new HashSet<Rol>();
        roles.add(admin);

        //int id, String nombre, String apellido, String email, String password, String username, Set<Rol> roles
        Usuario usuario1 = new Usuario(1,"Joel","Spítale","vspitale107@alumnos.iua.edu.ar","123","jspitale97",roles);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(usuario1, null,usuario1.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        Authentication auth1 = SecurityContextHolder.getContext().getAuthentication();
        usuario1 = (Usuario) auth1.getPrincipal();

        AuthToken newToken = new AuthToken(usuario1.getDuracionToken(), usuario1.getUsername());

        String token = newToken.encodeCookieValue();
        token = token.replace("[", "").replace("]", "");

        return token;
    }
}
