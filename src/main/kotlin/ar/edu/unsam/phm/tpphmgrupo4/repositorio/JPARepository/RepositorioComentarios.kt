package ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository

import ar.edu.unsam.phm.tpphmgrupo4.domain.Comentario
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface RepositorioComentarios:CrudRepository<Comentario, Long>  {
    @Query("SELECT AVG(c.puntuacion) FROM Comentario c WHERE c.idShow = :idShow")
    fun obtenerPromedioPuntuacionPorIdShow(@Param("idShow") idShow: String): Double
}