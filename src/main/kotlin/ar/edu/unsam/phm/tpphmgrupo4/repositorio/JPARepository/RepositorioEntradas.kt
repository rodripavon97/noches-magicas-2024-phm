package ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository

import ar.edu.unsam.phm.tpphmgrupo4.domain.Entrada
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

}