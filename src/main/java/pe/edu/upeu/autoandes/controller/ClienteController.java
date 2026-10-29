package pe.edu.upeu.autoandes.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.autoandes.dto.ClienteResponseDTO;
import pe.edu.upeu.autoandes.service.ClienteService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Consulta de clientes")
public class ClienteController {
    private final ClienteService service;

    @GetMapping
    @PreAuthorize("hasAuthority('VEHICULO_LEER')")
    public List<ClienteResponseDTO> listar() {
        return service.listar().stream()
                .map(c -> new ClienteResponseDTO(c.getId(), c.getDocumento(), c.getNombres(), c.getTelefono()))
                .toList();
    }
}
