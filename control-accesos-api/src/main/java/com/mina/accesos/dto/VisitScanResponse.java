package com.mina.accesos.dto;

import java.time.LocalDateTime;

public record VisitScanResponse(
        boolean entrada,
        String fullName,
        String qrCode,
        LocalDateTime fechaHoraEntrada,
        LocalDateTime fechaHoraSalida
) {
    public static VisitScanResponse entry(String fullName, String qrCode, LocalDateTime fechaHoraEntrada) {
        return new VisitScanResponse(true, fullName, qrCode, fechaHoraEntrada, null);
    }

    public static VisitScanResponse exit(String fullName, String qrCode,
                                         LocalDateTime fechaHoraEntrada, LocalDateTime fechaHoraSalida) {
        return new VisitScanResponse(false, fullName, qrCode, fechaHoraEntrada, fechaHoraSalida);
    }
}
