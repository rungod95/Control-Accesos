package com.mina.accesos.dto;

import java.util.List;

public record AccessSummaryResponse(
        long total,
        long activos,
        long hoy,
        long ultimaSemana,
        List<AccessByTypeResponse> porTipo
) {
}
