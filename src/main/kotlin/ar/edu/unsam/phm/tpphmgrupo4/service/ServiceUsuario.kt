package ar.edu.unsam.phm.tpphmgrupo4.service

import ar.edu.unsam.phm.tpphmgrupo4.DTO.UsuarioDTO
import ar.edu.unsam.phm.tpphmgrupo4.domain.UsuarioComun
import ar.edu.unsam.phm.tpphmgrupo4.domain.node.UsuarioNodo
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioUsuarioComun
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.NeoRepository.RepositorioUsuarioComunNeo4j
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Servicio responsable de las operaciones básicas de usuarios.
 * Refactorizado siguiendo el principio SRP (Single Responsibility Principle).
 * 
 * Responsabilidades:
 * - Consulta de datos de usuario
 * - Actualización de datos personales
 * - Gestión de saldo/crédito
 * 
 * Otras responsabilidades fueron delegadas a servicios especializados:
 * - AuthenticationService: login/autenticación
 * - CarritoService: gestión de carritos
 * - CompraService: proceso de compra
 * - AmistadService: gestión de amigos
 * - ComentarioService: gestión de comentarios
 */
@Service
class ServiceUsuario {
    @Autowired
    lateinit var repositorioUsuarioComun: RepositorioUsuarioComun
    
    @Autowired
    lateinit var repositorioUsuarioNeo: RepositorioUsuarioComunNeo4j

    /**
     * Obtiene un usuario por su ID.
     * @param id ID del usuario
     * @return Usuario encontrado
     */
    fun getUserByID(id: Long): UsuarioComun {
        return repositorioUsuarioComun.findById(id).get()
    }

    /**
     * Obtiene un nodo de usuario en Neo4j por su nombre de usuario.
     * @param nombre Nombre de usuario
     * @return Nodo de usuario
     */
    fun getUserNeoByName(nombre: String): UsuarioNodo {
        return repositorioUsuarioNeo.findUsuarioNodoByUsername(nombre)
    }

    /**
     * Obtiene los datos completos de un usuario por su ID.
     * @param id ID del usuario
     * @return DTO con datos del usuario
     */
    @Transactional(Transactional.TxType.NEVER)
    fun getDataUserByID(id: Int): UsuarioDTO {
        return UsuarioDTO.fromUsuario(repositorioUsuarioComun.findById(id.toLong()).get())
    }

    /**
     * Edita los datos personales de un usuario (nombre y apellido).
     * @param idUsuario ID del usuario
     * @param nombre Nuevo nombre
     * @param apellido Nuevo apellido
     */
    @Transactional(Transactional.TxType.NEVER)
    fun editarDatos(idUsuario: Int, nombre: String, apellido: String) {
        val usuario = repositorioUsuarioComun.findEditById(idUsuario.toLong()).get()
        usuario.cambiarNombres(nombre, apellido)
        repositorioUsuarioComun.save(usuario)
    }

    /**
     * Agrega crédito al saldo de un usuario.
     * @param idUsuario ID del usuario
     * @param credito Monto a agregar
     */
    fun sumarCredito(idUsuario: Long, credito: Double) {
        val usuario = getUserByID(idUsuario)
        usuario.aumentarSaldo(credito)
        repositorioUsuarioComun.save(usuario)
    }
}
