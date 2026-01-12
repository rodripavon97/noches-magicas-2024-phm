package ar.edu.unsam.phm.tpphmgrupo4.service

import ar.edu.unsam.phm.tpphmgrupo4.domain.Comentario
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioComentarios
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioUsuarioComun
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.MongoRepository.RepositorioShow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Servicio responsable de la gestión de comentarios y puntuaciones.
 * Principio SRP: Una sola responsabilidad - manejar comentarios y ratings
 */
@Service
class ComentarioService {
    @Autowired
    lateinit var repositorioUsuarioComun: RepositorioUsuarioComun
    
    @Autowired
    lateinit var repositorioShow: RepositorioShow
    
    @Autowired
    lateinit var repositorioComentarios: RepositorioComentarios

    /**
     * Obtiene los comentarios de un usuario específico.
     * @param idUsuario ID del usuario
     * @return Lista de comentarios del usuario
     */
    fun listaComentarios(idUsuario: Int): List<Comentario> {
        return repositorioUsuarioComun.findComentariosByUserId(idUsuario.toLong())
    }

    /**
     * Obtiene los comentarios de un show específico.
     * @param showId ID del show
     * @return Lista de comentarios del show
     */
    fun traerComentarios(showId: String): List<Comentario> {
        return repositorioUsuarioComun.findComentariosDeShow(showId)
    }

    /**
     * Permite a un usuario dejar un comentario en un show.
     * Actualiza automáticamente el puntaje del show.
     * @param idUsuario ID del usuario
     * @param idShow ID del show
     * @param comentario Texto del comentario
     * @param puntuacion Puntuación (0-5)
     */
    fun dejarComentario(idUsuario: Long, idShow: String, comentario: String, puntuacion: Float) {
        val usuario = repositorioUsuarioComun.findById(idUsuario).get()
        val show = repositorioShow.findById(idShow).get()
        
        usuario.dejarComentario(show, comentario, puntuacion)
        repositorioUsuarioComun.save(usuario)
        
        actualizarPuntajeShow(idShow)
    }

    /**
     * Borra un comentario de un usuario en un show.
     * Actualiza automáticamente el puntaje del show.
     * @param idUsuario ID del usuario
     * @param idShow ID del show
     */
    fun borrarComentario(idUsuario: Long, idShow: String) {
        val usuario = repositorioUsuarioComun.findById(idUsuario).get()
        usuario.borrarComentario(idShow)
        repositorioUsuarioComun.save(usuario)
        
        actualizarPuntajeShow(idShow)
    }

    /**
     * Actualiza el puntaje promedio y cantidad de comentarios de un show.
     * @param idShow ID del show
     */
    private fun actualizarPuntajeShow(idShow: String) {
        val show = repositorioShow.findById(idShow).get()
        val comentarios = repositorioUsuarioComun.findComentariosDeShow(idShow)
        val puntaje = repositorioComentarios.obtenerPromedioPuntuacionPorIdShow(idShow)
        
        show.actualizarComentarios(puntaje, comentarios.size)
        repositorioShow.save(show)
    }
}
