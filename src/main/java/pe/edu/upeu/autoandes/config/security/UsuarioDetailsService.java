package pe.edu.upeu.autoandes.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import pe.edu.upeu.autoandes.entity.Usuario;
import pe.edu.upeu.autoandes.repository.UsuarioRepository;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository repository;
    public Usuario load(String username) {
        return repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas"));
    }
    @Override public UserDetails loadUserByUsername(String username) {
        Usuario u = load(username);
        Set<String> permissions = new HashSet<>();
        Set<String> roles = u.getRoles() == null ? Set.of() : u.getRoles().stream().map(r -> r.getNombre()).collect(java.util.stream.Collectors.toSet());
        roles.forEach(r -> permissions.addAll(PermisosPorRol.para(r)));
        List<SimpleGrantedAuthority> auth = Stream.concat(roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)),
                permissions.stream().map(SimpleGrantedAuthority::new)).toList();
        return User.withUsername(u.getUsername()).password(u.getPassword()).disabled(!Boolean.TRUE.equals(u.getActivo())).authorities(auth).build();
    }
}
