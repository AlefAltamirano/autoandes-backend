package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orden_servicio")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenServicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero", nullable = false, unique = true)
    private String numero;
    
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
    
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.NUMERIC)
    private Double kilometraje;
    
    @Column(nullable = false)
    private String estado; // REGISTRADA, ANULADA

    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.NUMERIC)
    private Double total;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @Column(name = "usuario_registro", nullable = false)
    private String usuarioRegistro;

    @Column(name = "motivo_anulacion")
    private String motivoAnulacion;
    
    @OneToMany(mappedBy = "ordenServicio", cascade = CascadeType.ALL)
    private List<DetalleOrden> detalles;
}

