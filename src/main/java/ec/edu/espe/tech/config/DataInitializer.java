package ec.edu.espe.tech.config;

import ec.edu.espe.tech.entity.CategoriaHardware;
import ec.edu.espe.tech.entity.EstadoHardware;
import ec.edu.espe.tech.entity.HardwareEntity;
import ec.edu.espe.tech.repository.HardwareRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner cargarHardwareInicial(HardwareRepository hardwareRepository) {
        return args -> {
            if (hardwareRepository.count() > 0) {
                return;
            }

            Random random = new Random(2026);
            CategoriaHardware[] categorias = CategoriaHardware.values();
            List<HardwareEntity> equipos = new ArrayList<>(10_000);

            for (int i = 1; i <= 10_000; i++) {
                CategoriaHardware categoria = categorias[random.nextInt(categorias.length)];
                EstadoHardware estado = random.nextDouble() < 0.82
                        ? EstadoHardware.ACTIVO
                        : EstadoHardware.DEBAJA;

                BigDecimal precio = BigDecimal.valueOf(350 + random.nextDouble() * 4650)
                        .setScale(2, RoundingMode.HALF_UP);

                LocalDate fechaCompra = LocalDate.now().minusDays(random.nextInt(365 * 8));

                HardwareEntity equipo = new HardwareEntity(
                        null,
                        "ESPE-" + categoria.name() + "-" + i,
                        categoria,
                        precio,
                        fechaCompra,
                        estado
                );

                equipos.add(equipo);
            }

            hardwareRepository.saveAll(equipos);
        };
    }
}
