package com.mina.accesos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_log")
@Getter @Setter
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombrePersona;

    @NotBlank
    @Pattern(regexp = "trabajador|visitante|administrador", message = "tipoUsuario debe ser 'trabajador', 'visitante' o 'administrador'")
    private String tipoUsuario; // trabajador / visitante / administrador

    private String motivo;

    private LocalDateTime fechaHoraEntrada;

    private LocalDateTime fechaHoraSalida;

    // Código QR asociado a la persona/visita
    private String qrCode;
}
