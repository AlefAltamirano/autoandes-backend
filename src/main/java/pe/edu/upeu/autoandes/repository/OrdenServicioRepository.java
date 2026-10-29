package pe.edu.upeu.autoandes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.autoandes.entity.OrdenServicio;
import pe.edu.upeu.autoandes.entity.Vehiculo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrdenServicioRepository extends JpaRepository<OrdenServicio, Long> {

    @Query("SELECT o FROM OrdenServicio o WHERE " +
           "(:placa IS NULL OR o.vehiculo.placa = :placa) AND " +
           "(:estado IS NULL OR o.estado = :estado) AND " +
           "(:desde IS NULL OR o.fechaRegistro >= :desde) AND " +
           "(:hasta IS NULL OR o.fechaRegistro <= :hasta)")
    List<OrdenServicio> buscarConFiltros(@Param("placa") String placa, 
                                        @Param("estado") String estado, 
                                        @Param("desde") LocalDateTime desde, 
                                        @Param("hasta") LocalDateTime hasta);

    Optional<OrdenServicio> findFirstByVehiculoOrderByFechaRegistroDesc(Vehiculo vehiculo);

    @Query("SELECT new pe.edu.upeu.autoandes.dto.ReporteIngresoServicioDTO(s.codigo, s.nombre, SUM(d.cantidad), SUM(d.subtotal)) " +
           "FROM DetalleOrden d JOIN d.servicio s JOIN d.ordenServicio o " +
           "WHERE o.estado != 'ANULADA' " +
           "GROUP BY s.codigo, s.nombre")
    List<Object[]> obtenerReporteIngresos(); // Se mapeará manualmente o usando proyección Interface
}
