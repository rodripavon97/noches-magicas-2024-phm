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
 * Principio SRP: Una sola responsabilidad - cálculos de recaudación y rentabilidad
 */
@Service
class RecaudacionService {
    @Autowired
    lateinit var repositorioShow: RepositorioShow
    
    @Autowired
    lateinit var repositorioEntrada: RepositorioEntradas
    
    @Autowired
    lateinit var repositorioInstalacion: RepositorioInstalacion

    /**
     * Calcula la recaudación total de un show.
     * @param showId ID del show
     * @return Monto total recaudado
     */
    fun calcularRecaudacionShow(showId: String): Double {
        val entradasVendidas = repositorioEntrada.entradasVendidasByShowId(showId)
        return entradasVendidas.sumOf { it.precioFinal() }
    }

    /**
     * Calcula el porcentaje de rentabilidad de un show.
     * Fórmula: ((recaudación - costo) / costo) * 100
     * @param idShow ID del show
     * @return Porcentaje de rentabilidad
     */
    fun calcularRentabilidadShow(idShow: String): Double {
        val show = repositorioShow.findById(idShow).get()
        val instalacion = repositorioInstalacion.findById(show.instalacionId).get()
        val costoDelShow = show.costoShow(instalacion)
        val recaudacionDelShow = calcularRecaudacionShow(idShow)

        return ((recaudacionDelShow - costoDelShow) / costoDelShow) * 100
    }

    /**
     * Cuenta las entradas vendidas de una ubicación específica para un show.
     * Optimizado: Cuenta en DB en lugar de cargar y filtrar en memoria.
     * @param showId ID del show
     * @param ubicacion Ubicación a consultar
     * @return Cantidad de entradas vendidas
     */
    fun entradasVendidasPorUbicacion(showId: String, ubicacion: Ubicacion): Long {
        return repositorioEntrada.contarEntradasVendidasPorUbicacion(showId, ubicacion)
    }

    /**
     * Obtiene un mapa con la cantidad de entradas vendidas por cada ubicación.
     * Optimizado: Cada conteo se hace con una query específica en DB.
     * @param showId ID del show
     * @return Mapa de ubicación a cantidad de entradas vendidas
     */
    fun obtenerEstadisticasPorUbicacion(showId: String): Map<Ubicacion, Int> {
        val show = repositorioShow.findById(showId).get()
        val instalacion = repositorioInstalacion.findById(show.instalacionId).get()
        
        // Cada conteo se hace con una query optimizada en DB
        return instalacion.categorias.associateWith { ubicacion ->
            entradasVendidasPorUbicacion(showId, ubicacion).toInt()
        }
    }
}
