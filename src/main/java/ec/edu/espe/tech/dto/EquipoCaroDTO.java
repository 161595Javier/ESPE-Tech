package ec.edu.espe.tech.dto;

import ec.edu.espe.tech.entity.CategoriaHardware;
import ec.edu.espe.tech.entity.EstadoHardware;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EquipoCaroDTO(
        Long id,
        String modelo,
        CategoriaHardware categoria,
        BigDecimal precio,
        LocalDate fechaCompra,
        EstadoHardware estado
) {
}
