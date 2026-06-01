package ec.edu.espe.tech.service;

import ec.edu.espe.tech.dto.CategoriaResumenDTO;
import ec.edu.espe.tech.entity.CategoriaHardware;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;

@Service
public class AiService {

    public String generarResumenInventario(String enfoque, Map<CategoriaHardware, CategoriaResumenDTO> resumen) {
        long totalEquipos = resumen.values()
                .stream()
                .mapToLong(CategoriaResumenDTO::cantidadEquipos)
                .sum();

        BigDecimal valorTotal = resumen.values()
                .stream()
                .map(CategoriaResumenDTO::valorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String categoriaMayorValor = resumen.values()
                .stream()
                .max(Comparator.comparing(CategoriaResumenDTO::valorTotal))
                .map(item -> item.categoria().name())
                .orElse("SIN_DATOS");

        return "Resumen de Inventario (" + enfoque + "): se analizaron " + totalEquipos
                + " equipos activos comprados en los ultimos 5 anios. El valor total filtrado es $"
                + valorTotal + ". La categoria con mayor valor acumulado es " + categoriaMayorValor + ".";
    }
}
