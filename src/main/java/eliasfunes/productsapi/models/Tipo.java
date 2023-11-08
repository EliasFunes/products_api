package eliasfunes.productsapi.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "tipos")
public class Tipo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nombre", unique = true, nullable = false)
    private String nombre;

    @Column(name = "creado_en", nullable = false, columnDefinition = "timestamp default now()")
    @CreatedDate
    private Instant createdDate;

    @Column(name = "usuario", nullable = false)
    private String usuario;

}
