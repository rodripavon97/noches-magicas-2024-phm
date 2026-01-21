package ar.edu.unsam.phm.tpphmgrupo4.service

import ar.edu.unsam.phm.tpphmgrupo4.DTO.UsuarioDTO
import ar.edu.unsam.phm.tpphmgrupo4.domain.UsuarioComun
import ar.edu.unsam.phm.tpphmgrupo4.domain.node.UsuarioNodo
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioUsuarioComun
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.NeoRepository.RepositorioUsuarioComunNeo4j
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

/**
 * Servicio responsable de las operaciones básicas de usuarios.
 * 
 * Principios aplicados:
 * - SRP: Una sola responsabilidad - operaciones básicas de usuario
 * - DIP: Depende de abstracciones (repositorios inyectados)
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
class ServiceUsuario(
    private val repositorioUsuarioComun: RepositorioUsuarioComun,
    private val repositorioUsuarioNeo: RepositorioUsuarioComunNeo4j
) {

    fun findUsuarioById(usuarioId: Long): UsuarioComun {
        return repositorioUsuarioComun.findById(usuarioId)
            .orElseThrow { NoSuchElementException("Usuario no encontrado con ID: $usuarioId") }
    }

    fun findUsuarioNodoByUsername(username: String): UsuarioNodo {
        return repositorioUsuarioNeo.findUsuarioNodoByUsername(username)
    }

    @Transactional(Transactional.TxType.NEVER)
    fun obtenerDatosUsuario(usuarioId: Int): UsuarioDTO {
        val usuario = repositorioUsuarioComun.findById(usuarioId.toLong())
            .orElseThrow { NoSuchElementException("Usuario no encontrado con ID: $usuarioId") }
        return UsuarioDTO.fromUsuario(usuario)
    }

    @Transactional(Transactional.TxType.NEVER)
    fun actualizarDatosPersonales(usuarioId: Int, nombre: String, apellido: String) {
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(apellido.isNotBlank()) { "El apellido no puede estar vacío" }
        
        val usuario = repositorioUsuarioComun.findEditById(usuarioId.toLong())
            .orElseThrow { NoSuchElementException("Usuario no encontrado con ID: $usuarioId") }
        usuario.cambiarNombres(nombre, apellido)
        repositorioUsuarioComun.save(usuario)
    }

    fun agregarCredito(usuarioId: Long, monto: Double) {
        require(monto > 0) { "El monto debe ser mayor a cero" }
        
        val usuario = findUsuarioById(usuarioId)
        usuario.aumentarSaldo(monto)
        repositorioUsuarioComun.save(usuario)
    }
}
