package pe.edu.upeu.autoandes.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.upeu.autoandes.entity.Cliente;
import pe.edu.upeu.autoandes.repository.ClienteRepository;
import pe.edu.upeu.autoandes.service.ClienteService;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {
    private final ClienteRepository repository;

    @Override
    public List<Cliente> listar() {
        return repository.findAll();
    }
}
