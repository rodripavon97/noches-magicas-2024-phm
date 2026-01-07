package ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository

import ar.edu.unsam.phm.tpphmgrupo4.domain.Comentario
import ar.edu.unsam.phm.tpphmgrupo4.domain.UsuarioComun
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*

interface RepositorioUsuarioComun : CrudRepository<UsuarioComun, Long> {
    fun findByUsernameAndPassword(username: String, password: String): Optional<UsuarioComun>

    fun findEditById(id : Long) : Optional<UsuarioComun>

    @EntityGraph(
        attributePaths=[
           "amigos", "entradasCompradas", "comentarios"
        ]
    )
    override fun findById(id: Long): Optional<UsuarioComun>

    @Query("""
        SELECT amigo 
        FROM UsuarioComun u 
        JOIN u.amigos amigoId 
        JOIN UsuarioComun amigo ON amigo.id = amigoId 
        JOIN amigo.entradasCompradas entrada 
        WHERE u.id = :idUsuario 
        AND entrada.showId = :showId
    """)
    fun findAmigosQueVanAlShow(@Param("idUsuario") idUsuario: Long, @Param("showId") showId: String): List<UsuarioComun>

    @Query("""
        SELECT comentarios
        FROM UsuarioComun u
        JOIN u.comentarios comentarios
        WHERE comentarios.idShow = :showId
    """)
    fun findComentariosDeShow(@Param("showId") showId: String): List<Comentario>

    @Query("""
        SELECT c
        FROM UsuarioComun u
        JOIN u.comentarios c
        WHERE u.id = :userId
    """)
    fun findComentariosByUserId(@Param("userId") userId: Long): List<Comentario>
}