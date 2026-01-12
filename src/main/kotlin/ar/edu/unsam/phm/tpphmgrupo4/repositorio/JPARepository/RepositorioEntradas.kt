package ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository

import ar.edu.unsam.phm.tpphmgrupo4.domain.Entrada
import ar.edu.unsam.phm.tpphmgrupo4.domain.Ubicacion
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface RepositorioEntradas : CrudRepository<Entrada, Long> {
    fun findEntradaByShowId(showId: String): List<Entrada>

    fun findEntradaByFuncionId(funcionId: String): List<Entrada>

    @Query("SELECT e FROM Entrada e WHERE e.showId = :showId AND e.estaVendida = true")
    fun entradasVendidasByShowId(@Param("showId") showId: String): List<Entrada>
    
    @Query("SELECT e FROM Entrada e WHERE e.showId = :showId AND e.funcionId = :funcionId")
    fun findEntradasByShowIdAndFuncionId(@Param("showId") showId : String, @Param("funcionId") funcionId : String) : List <Entrada>
    
    @Query(
        """
        SELECT COUNT(e) = (
            SELECT COUNT(e2) 
            FROM Entrada e2 
            WHERE e2.showId = :showId
        ) 
        FROM Entrada e 
        WHERE e.showId = :showId AND e.estaVendida = true
    """
    )
    fun todasLasEntradasVendidas(@Param("showId") showId: String): Boolean

    /**
     * Obtiene entradas disponibles (no vendidas) para un show, función y ubicación específica.
     * Optimizado para evitar filtrar en memoria.
     * Limitado a la cantidad solicitada.
     */
    @Query("""
        SELECT e 
        FROM Entrada e 
        WHERE e.showId = :showId 
        AND e.funcionId = :funcionId 
        AND e.ubicacion = :ubicacion 
        AND e.estaVendida = false
    """)
    fun findEntradasDisponibles(
        @Param("showId") showId: String,
        @Param("funcionId") funcionId: String,
        @Param("ubicacion") ubicacion: Ubicacion
    ): List<Entrada>

    /**
     * Cuenta entradas vendidas por ubicación para un show.
     * Optimizado para hacer el conteo en base de datos.
     */
    @Query("""
        SELECT COUNT(e) 
        FROM Entrada e 
        WHERE e.showId = :showId 
        AND e.ubicacion = :ubicacion 
        AND e.estaVendida = true
    """)
    fun contarEntradasVendidasPorUbicacion(
        @Param("showId") showId: String,
        @Param("ubicacion") ubicacion: Ubicacion
    ): Long

    /**
     * Verifica si todas las entradas de una función están vendidas.
     * Optimizado para hacer la verificación en base de datos.
     */
    @Query("""
        SELECT CASE WHEN COUNT(e) = 0 THEN true ELSE false END
        FROM Entrada e 
        WHERE e.funcionId = :funcionId 
        AND e.estaVendida = false
    """)
    fun todasEntradasVendidasPorFuncion(@Param("funcionId") funcionId: String): Boolean

}