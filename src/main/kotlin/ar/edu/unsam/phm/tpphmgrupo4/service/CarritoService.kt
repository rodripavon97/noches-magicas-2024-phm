package ar.edu.unsam.phm.tpphmgrupo4.service

import ar.edu.unsam.phm.tpphmgrupo4.domain.Carrito
import ar.edu.unsam.phm.tpphmgrupo4.domain.Entrada
import ar.edu.unsam.phm.tpphmgrupo4.domain.Ubicacion
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioEntradas
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioUsuarioComun
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.MongoRepository.RepositorioShow
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.RedisRepository.RepositorioCarrito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Servicio responsable de la gestión del carrito de compras.
 * 
 * Principios aplicados:
 * - SRP: Una sola responsabilidad - manejar operaciones del carrito
 * - DIP: Depende de abstracciones (repositorios inyectados)
 */
@Service
class CarritoService(
    private val repositorioCarrito: RepositorioCarrito,
    private val repositorioUsuarioComun: RepositorioUsuarioComun,
    private val repositorioShow: RepositorioShow,
    private val repositorioEntrada: RepositorioEntradas
) {

    fun getCarritoById(idUsuario: Long): Carrito {
        return repositorioCarrito.findById(idUsuario)
            .orElseGet {
                val usuario = repositorioUsuarioComun.findById(idUsuario)
                    .orElseThrow { NoSuchElementException("Usuario no encontrado con ID: $idUsuario") }
                val carritoNuevo = Carrito(usuario.id.toInt(), mutableListOf())
                repositorioCarrito.save(carritoNuevo)
            }
    }

    fun agregarAlCarrito(idUsuario: Long, idShow: String, idFuncion: Int, cantidad: Int, ubi: Ubicacion) {
        require(cantidad > 0) { "La cantidad debe ser mayor que cero" }
        
        val carrito = getCarritoById(idUsuario)
        val show = repositorioShow.findById(idShow)
            .orElseThrow { NoSuchElementException("Show no encontrado con ID: $idShow") }
        val funcion = show.funciones[idFuncion]
        
        val entradasDisponibles = repositorioEntrada.findEntradasDisponibles(idShow, funcion.id, ubi)
        
        if (entradasDisponibles.size < cantidad) {
            throw RuntimeException("No hay suficientes entradas disponibles. Disponibles: ${entradasDisponibles.size}, Solicitadas: $cantidad")
        }
        
        val entradasCarrito = entradasDisponibles.take(cantidad)
        carrito.agregarEntradasACarrito(entradasCarrito)
        entradasCarrito.forEach { it.vender() }
        
        repositorioCarrito.save(carrito)
        repositorioEntrada.saveAll(entradasCarrito)
    }

    fun obtenerEntradasCarrito(idUsuario: Long): List<Entrada> {
        return getCarritoById(idUsuario).items
    }

    fun vaciarCarrito(idUsuario: Long) {
        val carrito = getCarritoById(idUsuario)
        carrito.items.clear()
        repositorioCarrito.save(carrito)
    }
}
