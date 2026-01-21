package ar.edu.unsam.phm.tpphmgrupo4.service

import ar.edu.unsam.phm.tpphmgrupo4.domain.Entrada
import ar.edu.unsam.phm.tpphmgrupo4.domain.Ubicacion
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioEntradas
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioInstalacion
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.MongoRepository.RepositorioShow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Servicio responsable de los cálculos financieros y estadísticas de shows.
 * 
 * Principios aplicados:
 * - SRP: Una sola responsabilidad - cálculos de recaudación y rentabilidad
 * - DIP: Depende de abstracciones (repositorios inyectados)
 */
@Service
class RecaudacionService(
    private val repositorioShow: RepositorioShow,
    private val repositorioEntrada: RepositorioEntradas,
    private val repositorioInstalacion: RepositorioInstalacion
) {

    fun calcularRecaudacionShow(showId: String): Double {
        val entradasVendidas = repositorioEntrada.entradasVendidasByShowId(showId)
        return entradasVendidas.sumOf { it.precioFinal() }
    }

    /**
     * Calcula el porcentaje de rentabilidad de un show.
     * Fórmula: ((recaudación - costo) / costo) * 100
     */
    fun calcularRentabilidadShow(idShow: String): Double {
        val show = repositorioShow.findById(idShow)
            .orElseThrow { NoSuchElementException("Show no encontrado con ID: $idShow") }
        val instalacion = repositorioInstalacion.findById(show.instalacionId)
            .orElseThrow { NoSuchElementException("Instalación no encontrada con ID: ${show.instalacionId}") }
        
        val costoDelShow = show.costoShow(instalacion)
        require(costoDelShow > 0) { "El costo del show debe ser mayor a cero" }
        
        val recaudacionDelShow = calcularRecaudacionShow(idShow)

        return ((recaudacionDelShow - costoDelShow) / costoDelShow) * 100
    }

    fun entradasVendidasPorUbicacion(showId: String, ubicacion: Ubicacion): Long {
        return repositorioEntrada.contarEntradasVendidasPorUbicacion(showId, ubicacion)
    }

    fun obtenerEstadisticasPorUbicacion(showId: String): Map<Ubicacion, Int> {
        val show = repositorioShow.findById(showId)
            .orElseThrow { NoSuchElementException("Show no encontrado con ID: $showId") }
        val instalacion = repositorioInstalacion.findById(show.instalacionId)
            .orElseThrow { NoSuchElementException("Instalación no encontrada con ID: ${show.instalacionId}") }
        
        return instalacion.categorias.associateWith { ubicacion ->
            entradasVendidasPorUbicacion(showId, ubicacion).toInt()
        }
    }
}
