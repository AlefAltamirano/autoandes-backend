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
    @Query(value = "SELECT orden_numero_seq.NEXTVAL FROM dual", nativeQuery = true)
    Long siguienteNumero();


    @Query("SELECT o FROM OrdenServicio o WHERE " +
           "(:placa IS NULL OR o.vehiculo.placa = :placa) AND " +
           "(:estado IS NULL OR o.estado = :estado) AND " +
           "(:desde IS NULL OR o.fecha >= :desde) AND " +
           "(:hasta IS NULL OR o.fecha <= :hasta) " +
           "ORDER BY o.fecha DESC")
    List<OrdenServicio> buscarConFiltros(@Param("placa") String placa, 
                                        @Param("estado") String estado, 
                                        @Param("desde") LocalDateTime desde, 
                                        @Param("hasta") LocalDateTime hasta);

    Optional<OrdenServicio> findFirstByVehiculoAndEstadoOrderByFechaDesc(Vehiculo vehiculo, String estado);

    boolean existsByVehiculoId(Long vehiculoId);

    boolean existsByDetallesServicioId(Long servicioId);

    @Query("SELECT new pe.edu.upeu.autoandes.dto.ReporteIngresoServicioDTO(s.codigo, s.nombre, SUM(d.cantidad), SUM(d.subtotal)) " +
           "FROM DetalleOrden d JOIN d.servicio s JOIN d.ordenServicio o " +
           "WHERE o.estado <> 'ANULADA' " +
           "GROUP BY s.codigo, s.nombre ORDER BY SUM(d.subtotal) DESC")
    List<pe.edu.upeu.autoandes.dto.ReporteIngresoServicioDTO> obtenerReporteIngresos();

    @Query("SELECT new pe.edu.upeu.autoandes.dto.ReporteIngresoServicioDTO(s.codigo, s.nombre, SUM(d.cantidad), SUM(d.subtotal)) " +
           "FROM DetalleOrden d JOIN d.servicio s JOIN d.ordenServicio o " +
           "WHERE o.estado <> 'ANULADA' AND (:desde IS NULL OR o.fecha >= :desde) AND (:hasta IS NULL OR o.fecha <= :hasta) " +
           "GROUP BY s.codigo, s.nombre ORDER BY SUM(d.subtotal) DESC")
    List<pe.edu.upeu.autoandes.dto.ReporteIngresoServicioDTO> obtenerReporteIngresos(
            @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
