package pe.edu.upeu.autoandes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.config.security.*;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.entity.Usuario;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager manager;
    private final JwtService jwt;
    private final UsuarioDetailsService users;
    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO r) {
        Authentication authentication = manager.authenticate(new UsernamePasswordAuthenticationToken(r.username(), r.password()));
        Usuario u = users.load(authentication.getName());
        Set<String> roles = u.getRoles() == null ? Set.of() : u.getRoles().stream().map(x -> x.getNombre()).collect(Collectors.toSet());
        Set<String> permissions = roles.stream().flatMap(x -> PermisosPorRol.para(x).stream()).collect(Collectors.toSet());
        return new LoginResponseDTO(jwt.generate(u.getUsername()), "Bearer", jwt.expiration(), u.getNombreCompleto(),
                roles.stream().sorted().toList(), permissions.stream().sorted().toList());
    }
    @GetMapping("/me")
    public UsuarioResponseDTO me(Authentication auth) {
        Usuario u = users.load(auth.getName());
        Set<String> roles = u.getRoles() == null ? Set.of() : u.getRoles().stream().map(x -> x.getNombre()).collect(Collectors.toSet());
        return new UsuarioResponseDTO(u.getId(), u.getUsername(), u.getNombreCompleto(), u.getActivo(),
                roles.stream().sorted().toList(), roles.stream().flatMap(x -> PermisosPorRol.para(x).stream()).distinct().sorted().toList());
    }
}
