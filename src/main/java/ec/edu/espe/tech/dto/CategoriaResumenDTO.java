package ec.edu.espe.tech.dto;

import ec.edu.espe.tech.entity.CategoriaHardware;

import java.math.BigDecimal;

public record CategoriaResumenDTO(
        CategoriaHardware categoria,
        long cantidadEquipos,
        BigDecimal valorTotal,
        BigDecimal promedioPrecio,
        EquipoCaroDTO equipoMasCaro
) {
}
