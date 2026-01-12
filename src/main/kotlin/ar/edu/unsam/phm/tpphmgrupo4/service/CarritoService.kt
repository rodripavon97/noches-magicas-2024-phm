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
 * Principio SRP: Una sola responsabilidad - manejar operaciones del carrito
 */
@Service
class CarritoService {
    @Autowired
    lateinit var repositorioCarrito: RepositorioCarrito
    
    @Autowired
    lateinit var repositorioUsuarioComun: RepositorioUsuarioComun
    
    @Autowired
    lateinit var repositorioShow: RepositorioShow
    
    @Autowired
    lateinit var repositorioEntrada: RepositorioEntradas

    /**
     * Obtiene el carrito de un usuario, creándolo si no existe.
     * @param idUsuario ID del usuario
     * @return Carrito del usuario
     */
    fun getCarritoById(idUsuario: Long): Carrito {
        return repositorioCarrito.findById(idUsuario)
            .orElseGet {
                val usuario = repositorioUsuarioComun.findById(idUsuario)
                    .orElseThrow { RuntimeException("Usuario no encontrado") }
                val carritoNuevo = Carrito(usuario.id.toInt(), mutableListOf())
                repositorioCarrito.save(carritoNuevo)
            }
    }

    /**
     * Agrega entradas al carrito de un usuario.
     * Optimizado: Query filtrada en DB en lugar de filtrar en memoria.
     * @param idUsuario ID del usuario
     * @param idShow ID del show
     * @param idFuncion Índice de la función
     * @param cantidad Cantidad de entradas a agregar
     * @param ubi Ubicación de las entradas
     */
    fun agregarAlCarrito(idUsuario: Long, idShow: String, idFuncion: Int, cantidad: Int, ubi: Ubicacion) {
        require(cantidad > 0) { "La cantidad debe ser mayor que cero" }
        
        val carrito = getCarritoById(idUsuario)
        val show = repositorioShow.findById(idShow).get()
        val funcion = show.funciones[idFuncion]
        
        // Query optimizada: filtra en DB en lugar de memoria
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

    /**
     * Obtiene las entradas en el carrito de un usuario.
     * @param idUsuario ID del usuario
     * @return Lista de entradas en el carrito
     */
    fun obtenerEntradasCarrito(idUsuario: Long): List<Entrada> {
        return getCarritoById(idUsuario).items
    }

    /**
     * Vacía el carrito de un usuario.
     * @param idUsuario ID del usuario
     */
    fun vaciarCarrito(idUsuario: Long) {
        val carrito = getCarritoById(idUsuario)
        carrito.items.clear()
        repositorioCarrito.save(carrito)
    }
}
