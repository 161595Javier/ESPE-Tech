package ec.edu.espe.tech.dto;

import ec.edu.espe.tech.entity.CategoriaHardware;

import java.time.LocalDate;
import java.util.Map;

public record InventarioResponseDTO(
        String enfoque,
        String resumenAI,
        LocalDate fechaCorte,
        Map<CategoriaHardware, CategoriaResumenDTO> resumenPorCategoria
) {
}
