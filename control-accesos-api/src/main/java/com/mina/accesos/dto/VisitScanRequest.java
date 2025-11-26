package com.mina.accesos.dto;

import jakarta.validation.constraints.NotBlank;

public record VisitScanRequest(
        @NotBlank(message = "qrCode es obligatorio")
        String qrCode
) {
}
