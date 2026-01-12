package ar.edu.unsam.phm.tpphmgrupo4.service

import ar.edu.unsam.phm.tpphmgrupo4.domain.UsuarioComun
import ar.edu.unsam.phm.tpphmgrupo4.domain.node.UsuarioNodo
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioUsuarioComun
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.NeoRepository.RepositorioUsuarioComunNeo4j
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Servicio responsable de la gestión de amistades entre usuarios.
 * Principio SRP: Una sola responsabilidad - manejar relaciones de amistad
 */
@Service
class AmistadService {
    @Autowired
    lateinit var repositorioUsuarioComun: RepositorioUsuarioComun
    
    @Autowired
    lateinit var repositorioUsuarioNeo: RepositorioUsuarioComunNeo4j

    /**
     * Obtiene la lista de amigos de un usuario.
     * Optimizado: Una sola query en lugar de N+1 queries.
     * @param idUsuario ID del usuario
     * @return Lista de amigos
     */
    @Transactional(Transactional.TxType.NEVER)
    fun listaAmigos(idUsuario: Long): List<UsuarioComun> {
        return repositorioUsuarioComun.findAmigosByUsuarioId(idUsuario)
    }

    /**
     * Agrega un amigo a la lista de un usuario.
     * Actualiza tanto la base de datos relacional como el grafo Neo4j.
     * @param idUsuario ID del usuario
     * @param idAmigo ID del amigo a agregar
     */
    fun agregarAmigo(idUsuario: Long, idAmigo: Int) {
        // Actualizar en la base de datos relacional
        val usuario = repositorioUsuarioComun.findById(idUsuario).get()
        usuario.agregarAmigo(idAmigo)
        repositorioUsuarioComun.save(usuario)

        // Actualizar en Neo4j
        val usuarioNeo = repositorioUsuarioNeo.findUsuarioNodoByUsername(usuario.username)
        val amigo = repositorioUsuarioComun.findById(idAmigo.toLong()).get()
        val amigoNeo = repositorioUsuarioNeo.findUsuarioNodoByUsername(amigo.username)
        usuarioNeo.agregarAmigo(amigoNeo)
        repositorioUsuarioNeo.save(usuarioNeo)
    }

    /**
     * Quita un amigo de la lista de un usuario.
     * Actualiza tanto la base de datos relacional como el grafo Neo4j.
     * @param idUsuario ID del usuario
     * @param idAmigo ID del amigo a quitar
     */
    fun quitarAmigo(idUsuario: Long, idAmigo: Int) {
        // Actualizar en la base de datos relacional
        val usuario = repositorioUsuarioComun.findById(idUsuario).get()
        usuario.quitarAmigo(idAmigo)
        repositorioUsuarioComun.save(usuario)

        // Actualizar en Neo4j
        val usuarioNeo = repositorioUsuarioNeo.findUsuarioNodoByUsername(usuario.username)
        val amigo = repositorioUsuarioComun.findById(idAmigo.toLong()).get()
        val amigoNeo = repositorioUsuarioNeo.findUsuarioNodoByUsername(amigo.username)
        usuarioNeo.quitarAmigo(amigoNeo)
        repositorioUsuarioNeo.save(usuarioNeo)
    }

    /**
     * Obtiene amigos sugeridos para un usuario basándose en el grafo de relaciones.
     * @param username Nombre de usuario
     * @return Lista de usuarios sugeridos
     */
    fun amiguesSugeridos(username: String): List<UsuarioNodo> {
        return repositorioUsuarioNeo.amiguesSugeridos(username)
    }

    /**
     * Obtiene los amigos de un usuario que van a un show específico.
     * Optimizado: Una sola query con JOIN en lugar de cargar todos y filtrar en memoria.
     * @param idUsuario ID del usuario
     * @param idShow ID del show
     * @return Lista de amigos que van al show
     */
    fun amigosQueVanAShow(idUsuario: Long, idShow: String): List<UsuarioComun> {
        return repositorioUsuarioComun.findAmigosQueVanAShowOptimizado(idUsuario, idShow)
    }
}
