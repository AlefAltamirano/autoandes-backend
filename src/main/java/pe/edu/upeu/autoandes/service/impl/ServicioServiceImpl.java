package pe.edu.upeu.autoandes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.upeu.autoandes.entity.Servicio;
import pe.edu.upeu.autoandes.exception.ReglaNegocioException;
import pe.edu.upeu.autoandes.repository.ServicioRepository;
import pe.edu.upeu.autoandes.service.ServicioService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository repository;

    @Override
    public List<Servicio> listar() {
        return repository.findAll();
    }

    @Override
    public Servicio guardar(Servicio servicio) {
        if (repository.existsByCodigo(servicio.getCodigo())) {
            log.warn("Intento de registrar código duplicado: {}", servicio.getCodigo());
            throw new ReglaNegocioException("El código ya existe");
        }
        log.info("Registrando nuevo servicio: {}", servicio.getCodigo());
        return repository.save(servicio);
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
        log.info("Servicio eliminado: {}", id);
    }

    @Override
    public void cambiarEstado(Long id, Boolean activo) {
        Servicio s = repository.findById(id).orElseThrow();
        s.setActivo(activo);
        repository.save(s);
        log.info("Servicio {} cambió estado a {}", id, activo);
    }
}
