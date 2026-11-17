package com.skillmaster.carrera;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "carreras")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    /** Distancia recorrida en metros. */
    private Double distanciaMetros;

    /** Duración total en segundos. */
    private Long duracionSegundos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCarrera estado;

    public enum EstadoCarrera {
        EN_CURSO, FINALIZADA, INVALIDADA
    }
}
