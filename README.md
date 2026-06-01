# ESPE-Tech Inventory
## Alex Canchignia
Modulo Spring Boot para gestionar y analizar inventario de equipos tecnologicos de laboratorio.

## Objetivo

Procesar 10.000 registros de hardware para generar reportes analiticos de disponibilidad y valoracion, aplicando dos paradigmas de programacion: imperativo y funcional/declarativo.

## Arquitectura

El proyecto sigue una arquitectura por capas:

- `entity`: contiene HardwareEntity, CategoriaHardware y EstadoHardware.
- `repository`: contiene HardwareRepository para la persistencia con Spring Data JPA.
- `service`: contiene la logica de negocio y los dos enfoques algorítmicos.
- `controller`: expone los endpoints REST.
- `dto`: estructura la respuesta del reporte.
- `config`: genera automaticamente 10.000 registros de prueba.

## Problema algorítmico

El sistema realiza las siguientes operaciones:

1. Filtra equipos en estado `ACTIVO`.
2. Filtra equipos comprados en los ultimos 5 años.
3. Agrupa equipos por categoria: `LAPTOP`, `PC`, `SERVIDOR`.
4. Calcula valor total por categoria.
5. Calcula promedio de precio por categoria.
6. Obtiene el equipo mas caro de cada categoria.
7. Genera un resumen dinamico mediante `AiService`.

## Endpoints

```http
GET http://localhost:8080/api/inventario/imperativo
GET http://localhost:8080/api/inventario/funcional
GET http://localhost:8080/api/inventario/comparativo
```

## Base de datos H2

```http
http://localhost:8080/h2-console
```

Datos de conexion:

- JDBC URL: `jdbc:h2:mem:espetechdb`
- User: `sa`
- Password: vacio

## Comparacion de enfoques

| Criterio | Enfoque imperativo | Enfoque funcional/declarativo |
|---|---|---|
| Estructura | Usa `for`, `if`, acumuladores manuales y mapas mutables. | Usa `Stream`, `filter`, `Collectors.groupingBy`, `summarizingDouble`, `reduce` y `Optional`. |
| Lineas de codigo | Requiere mas lineas porque el programador controla manualmente el recorrido, agrupacion y calculo. | Requiere menos lineas porque delega operaciones repetitivas a la API de Streams. |
| Legibilidad | Es claro para comprender paso a paso como se construyen los resultados. | Es mas expresivo para operaciones de filtrado, agrupacion y agregacion. |
| Mantenimiento | Si aumentan las reglas de negocio, puede crecer rapidamente y volverse mas dificil de modificar. | Es mas facil de extender cuando se agregan nuevos filtros o calculos agregados. |
| Control del algoritmo | Ofrece mayor control sobre cada acumulador y cada condicion. | Reduce codigo repetitivo y mejora la separacion entre que se quiere calcular y como se recorre la coleccion. |
| Riesgo de errores | Mayor riesgo por manejo manual de contadores, sumas y comparaciones. | Menor riesgo en operaciones de agregacion comunes, aunque requiere conocer bien Streams. |

## Conclusión

El enfoque imperativo permite observar con mayor detalle el proceso algorítmico, por lo que es util para explicar la logica paso a paso. Sin embargo, el enfoque funcional con Streams resulta mas legible y mantenible para tareas de analisis de datos, especialmente cuando se trabaja con filtros, agrupaciones y calculos por categoria. Para este caso el enfoque funcional es el mas adecuado porque el problema consiste principalmente en transformar y resumir un conjunto grande de registros.

## Repositorio

Link del repositorio GitHub:

```text
https://github.com/161595Javier/ESPE-Tech
```
