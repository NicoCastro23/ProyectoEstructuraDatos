package com.plataformaEducativa.proyectoestructuradatos.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "grupos_estudio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoEstudioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombreGrupo;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private boolean activo;

    // Relación con los estudiantes participantes
    @ManyToMany
    @JoinTable(
            name = "grupo_estudiantes",
            joinColumns = @JoinColumn(name = "grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "estudiante_id")
    )
    private Set<EstudianteEntity> estudiantes = new HashSet<>();

    // Relaciones con solicitudes de ayuda
    @OneToMany(mappedBy = "grupoEstudio", cascade = CascadeType.ALL)
    private List<SolicitudAyudaEntity> solicitudesAyuda = new ArrayList<>();

    // Método para agregar un participante
    public void agregarParticipante(EstudianteEntity estudiante) {
        this.estudiantes.add(estudiante);
    }

    // Método para eliminar un participante
    public void eliminarParticipante(EstudianteEntity estudiante) {
        this.estudiantes.remove(estudiante);
    }

    // Método para publicar contenido (solicitud de ayuda) en el grupo
    public SolicitudAyudaEntity publicarSolicitudAyuda(String contenido, EstudianteEntity estudiante) {
        SolicitudAyudaEntity solicitud = SolicitudAyudaEntity.builder()
                .contenido(contenido)
                .fechaCreacion(LocalDateTime.now())
                .leido(false)
                .estudiante(estudiante)
                .grupoEstudio(this)
                .build();

        this.solicitudesAyuda.add(solicitud);
        return solicitud;
    }

    // Método para verificar si un estudiante es parte del grupo
    public boolean esMiembro(EstudianteEntity estudiante) {
        return this.estudiantes.contains(estudiante);
    }

    // Métodos para activar/desactivar el grupo
    public void activar() {
        this.activo = true;
    }

    public void desactivar() {
        this.activo = false;
    }
}
