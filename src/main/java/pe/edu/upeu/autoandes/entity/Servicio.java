package pe.edu.upeu.autoandes.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "servicio")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String codigo;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    @JdbcTypeCode(SqlTypes.NUMERIC)
    private Double precio;

    @Column(name = "duracion_min", nullable = false)
    private Integer duracionMin;
    
    @Column(nullable = false)
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Builder.Default
    private Boolean activo = true;
}


