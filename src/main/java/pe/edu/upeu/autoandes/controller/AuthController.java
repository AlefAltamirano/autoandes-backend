package pe.edu.upeu.autoandes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.edu.upeu.autoandes.security.*;
import pe.edu.upeu.autoandes.config.JwtService;
import pe.edu.upeu.autoandes.config.JwtAuthenticationFilter;
import pe.edu.upeu.autoandes.dto.*;
import pe.edu.upeu.autoandes.entity.Usuario;
import pe.edu.upeu.autoandes.service.AuthService;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/auth", "/api/v1/auth"})
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Inicio de sesión y usuario actual")
public class AuthController {
    private final AuthService authService;
    private final UsuarioDetailsService users;
    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO r) {
        return authService.login(r);
    }
    @GetMapping("/me")
    public UsuarioResponseDTO me(Authentication auth) {
        Usuario u = users.load(auth.getName());
        Set<String> roles = u.getRoles() == null ? Set.of() : u.getRoles().stream().map(x -> x.getNombre()).collect(Collectors.toSet());
        return new UsuarioResponseDTO(u.getId(), u.getUsername(), u.getNombreCompleto(), u.getActivo(),
                roles.stream().sorted().toList(), roles.stream().flatMap(x -> PermisosPorRol.para(x).stream()).distinct().sorted().toList());
    }
}
