package ec.edu.espe.tech.controller;

import ec.edu.espe.tech.dto.InventarioResponseDTO;
import ec.edu.espe.tech.service.InventarioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping("/imperativo")
    public InventarioResponseDTO obtenerReporteImperativo() {
        return inventarioService.procesarImperativo();
    }

    @GetMapping("/funcional")
    public InventarioResponseDTO obtenerReporteFuncional() {
        return inventarioService.procesarFuncional();
    }

    @GetMapping("/comparativo")
    public Map<String, InventarioResponseDTO> obtenerComparativo() {
        return Map.of(
                "imperativo", inventarioService.procesarImperativo(),
                "funcional", inventarioService.procesarFuncional()
        );
    }
}
