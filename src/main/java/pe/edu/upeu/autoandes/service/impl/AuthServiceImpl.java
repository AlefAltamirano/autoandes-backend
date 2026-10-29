package pe.edu.upeu.autoandes.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.upeu.autoandes.config.JwtService;
import pe.edu.upeu.autoandes.security.PermisosPorRol;
import pe.edu.upeu.autoandes.dto.LoginRequestDTO;
import pe.edu.upeu.autoandes.dto.LoginResponseDTO;
import pe.edu.upeu.autoandes.entity.Usuario;
import pe.edu.upeu.autoandes.repository.UsuarioRepository;
import pe.edu.upeu.autoandes.service.AuthService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final String INVALID_CREDENTIALS = "Credenciales inválidas o cuenta inactiva";

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        String username = request.username() == null ? "" : request.username().trim();
        Usuario usuario = repository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new BadCredentialsException(INVALID_CREDENTIALS));

        if (!Boolean.TRUE.equals(usuario.getActivo())
                || !passwordEncoder.matches(request.password(), usuario.getPassword())) {
            throw new BadCredentialsException(INVALID_CREDENTIALS);
        }

        Set<String> roles = usuario.getRoles() == null ? Set.of() : usuario.getRoles().stream()
                .map(rol -> rol.getNombre()).collect(Collectors.toSet());
        Set<String> permisos = roles.stream()
                .flatMap(rol -> PermisosPorRol.para(rol).stream())
                .collect(Collectors.toSet());

        return new LoginResponseDTO(
                jwtService.generate(usuario.getUsername()),
                "Bearer",
                jwtService.expiration(),
                usuario.getNombreCompleto(),
                roles.stream().sorted().toList(),
                permisos.stream().sorted().toList());
    }
}
