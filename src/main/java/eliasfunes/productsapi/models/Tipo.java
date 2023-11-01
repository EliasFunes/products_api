package eliasfunes.productsapi.models;

import jakarta.persistence.*;
import lombok.*
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "tipos")
public class Tipo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nombre", unique = true, nullable = false)
    private String nombre;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "user", nullable = false)
    private String user;

}
