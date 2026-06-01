package ec.edu.espe.tech.service;

import ec.edu.espe.tech.dto.CategoriaResumenDTO;
import ec.edu.espe.tech.dto.EquipoCaroDTO;
import ec.edu.espe.tech.dto.InventarioResponseDTO;
import ec.edu.espe.tech.entity.CategoriaHardware;
import ec.edu.espe.tech.entity.EstadoHardware;
import ec.edu.espe.tech.entity.HardwareEntity;
import ec.edu.espe.tech.repository.HardwareRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventarioService {

    private final HardwareRepository hardwareRepository;
    private final AiService aiService;

    public InventarioService(HardwareRepository hardwareRepository, AiService aiService) {
        this.hardwareRepository = hardwareRepository;
        this.aiService = aiService;
    }

    public InventarioResponseDTO procesarImperativo() {
        LocalDate fechaCorte = LocalDate.now().minusYears(5);
        List<HardwareEntity> equipos = hardwareRepository.findAll();
        Map<CategoriaHardware, CategoriaAcumulador> acumuladores = new EnumMap<>(CategoriaHardware.class);

        for (HardwareEntity equipo : equipos) {
            if (cumpleFiltro(equipo, fechaCorte)) {
                CategoriaHardware categoria = equipo.getCategoria();

                CategoriaAcumulador acumulador = acumuladores.get(categoria);
                if (acumulador == null) {
                    acumulador = new CategoriaAcumulador(categoria);
                    acumuladores.put(categoria, acumulador);
                }

                acumulador.cantidad++;
                acumulador.valorTotal = acumulador.valorTotal.add(equipo.getPrecio());

                if (acumulador.equipoMasCaro == null
                        || equipo.getPrecio().compareTo(acumulador.equipoMasCaro.getPrecio()) > 0) {
                    acumulador.equipoMasCaro = equipo;
                }
            }
        }

        Map<CategoriaHardware, CategoriaResumenDTO> resumen = new EnumMap<>(CategoriaHardware.class);
        for (Map.Entry<CategoriaHardware, CategoriaAcumulador> entry : acumuladores.entrySet()) {
            CategoriaAcumulador acumulador = entry.getValue();
            BigDecimal promedio = calcularPromedio(acumulador.valorTotal, acumulador.cantidad);

            resumen.put(entry.getKey(), new CategoriaResumenDTO(
                    acumulador.categoria,
                    acumulador.cantidad,
                    acumulador.valorTotal,
                    promedio,
                    convertirEquipo(acumulador.equipoMasCaro)
            ));
        }

        return new InventarioResponseDTO(
                "IMPERATIVO",
                aiService.generarResumenInventario("IMPERATIVO", resumen),
                fechaCorte,
                resumen
        );
    }

    public InventarioResponseDTO procesarFuncional() {
        LocalDate fechaCorte = LocalDate.now().minusYears(5);

        Map<CategoriaHardware, List<HardwareEntity>> equiposPorCategoria = hardwareRepository.findAll()
                .stream()
                .filter(equipo -> cumpleFiltro(equipo, fechaCorte))
                .collect(Collectors.groupingBy(
                        HardwareEntity::getCategoria,
                        () -> new EnumMap<>(CategoriaHardware.class),
                        Collectors.toList()
                ));

        Map<CategoriaHardware, CategoriaResumenDTO> resumen = equiposPorCategoria.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> crearResumenFuncional(entry.getKey(), entry.getValue()),
                        (actual, reemplazo) -> actual,
                        () -> new EnumMap<>(CategoriaHardware.class)
                ));

        return new InventarioResponseDTO(
                "FUNCIONAL_STREAMS",
                aiService.generarResumenInventario("FUNCIONAL_STREAMS", resumen),
                fechaCorte,
                resumen
        );
    }

    private CategoriaResumenDTO crearResumenFuncional(CategoriaHardware categoria, List<HardwareEntity> equipos) {
        DoubleSummaryStatistics estadistica = equipos.stream()
                .collect(Collectors.summarizingDouble(equipo -> equipo.getPrecio().doubleValue()));

        BigDecimal valorTotal = equipos.stream()
                .map(HardwareEntity::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Optional<HardwareEntity> equipoMasCaro = equipos.stream()
                .max(Comparator.comparing(HardwareEntity::getPrecio));

        BigDecimal promedio = BigDecimal.valueOf(estadistica.getAverage())
                .setScale(2, RoundingMode.HALF_UP);

        return new CategoriaResumenDTO(
                categoria,
                estadistica.getCount(),
                valorTotal,
                promedio,
                equipoMasCaro.map(this::convertirEquipo).orElse(null)
        );
    }

    private boolean cumpleFiltro(HardwareEntity equipo, LocalDate fechaCorte) {
        return equipo.getEstado() == EstadoHardware.ACTIVO
                && equipo.getFechaCompra() != null
                && !equipo.getFechaCompra().isBefore(fechaCorte);
    }

    private BigDecimal calcularPromedio(BigDecimal valorTotal, long cantidad) {
        if (cantidad == 0) {
            return BigDecimal.ZERO;
        }
        return valorTotal.divide(BigDecimal.valueOf(cantidad), 2, RoundingMode.HALF_UP);
    }

    private EquipoCaroDTO convertirEquipo(HardwareEntity equipo) {
        if (equipo == null) {
            return null;
        }
        return new EquipoCaroDTO(
                equipo.getId(),
                equipo.getModelo(),
                equipo.getCategoria(),
                equipo.getPrecio(),
                equipo.getFechaCompra(),
                equipo.getEstado()
        );
    }

    private static class CategoriaAcumulador {
        private final CategoriaHardware categoria;
        private long cantidad;
        private BigDecimal valorTotal;
        private HardwareEntity equipoMasCaro;

        private CategoriaAcumulador(CategoriaHardware categoria) {
            this.categoria = categoria;
            this.cantidad = 0;
            this.valorTotal = BigDecimal.ZERO;
        }
    }
}
