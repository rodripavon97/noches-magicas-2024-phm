package ar.edu.unsam.phm.tpphmgrupo4.service

import ar.edu.unsam.phm.tpphmgrupo4.domain.Entrada
import ar.edu.unsam.phm.tpphmgrupo4.domain.Show
import ar.edu.unsam.phm.tpphmgrupo4.domain.UsuarioComun
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioEntradas
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioUsuarioComun
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.MongoRepository.RepositorioShow
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.NeoRepository.RepositorioShowNeo4j
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.NeoRepository.RepositorioUsuarioComunNeo4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Servicio responsable del proceso de compra de entradas.
 * Principio SRP: Una sola responsabilidad - gestionar la compra de entradas
 */
@Service
class CompraService {
    @Autowired
    lateinit var repositorioUsuarioComun: RepositorioUsuarioComun
    
    @Autowired
    lateinit var repositorioShow: RepositorioShow
    
    @Autowired
    lateinit var repositorioEntrada: RepositorioEntradas
    
    @Autowired
    lateinit var repositorioUsuarioNeo: RepositorioUsuarioComunNeo4j
    
    @Autowired
    lateinit var repositorioShowNeo: RepositorioShowNeo4j
    
    @Autowired
    lateinit var carritoService: CarritoService

    /**
     * Procesa la compra de entradas desde el carrito del usuario.
     * Optimizado: Minimiza iteraciones y usa queries específicas.
     * @param idUsuario ID del usuario que realiza la compra
     */
    fun comprarEntradas(idUsuario: Long) {
        val usuario = repositorioUsuarioComun.findById(idUsuario).get()
        val usuarioNeo = repositorioUsuarioNeo.findUsuarioNodoByUsername(usuario.username)
        val carrito = carritoService.getCarritoById(idUsuario)
        
        // Validar precios y obtener shows únicos
        val showsMap = mutableMapOf<String, Show>()
        carrito.items.forEach { entrada ->
            verificarPrecio(entrada)
            if (!showsMap.containsKey(entrada.showId)) {
                showsMap[entrada.showId] = repositorioShow.findById(entrada.showId).get()
            }
        }
        
        // Procesar la compra
        carrito.comprarEntradas(usuario)
        
        // Actualizar el estado de las funciones y relaciones en Neo4j
        // Optimizado: Usa query para verificar si función está agotada en DB
        showsMap.values.forEach { show ->
            val showNeo = repositorioShowNeo.findShowNodoByShowId(show.id)
            
            show.funciones.forEach { funcion ->
                // Query optimizada: verifica en DB si todas están vendidas
                if (repositorioEntrada.todasEntradasVendidasPorFuncion(funcion.id)) {
                    funcion.funcionAgotada()
                }
                
                // Solo agregar entradas del carrito al grafo Neo4j
                carrito.items
                    .filter { it.funcionId == funcion.id }
                    .forEach { entrada -> usuarioNeo.agregarEntrada(showNeo, entrada) }
            }
        }
        
        // Persistir cambios
        repositorioShow.saveAll(showsMap.values)
        repositorioEntrada.saveAll(carrito.items)
        repositorioUsuarioComun.save(usuario)
        repositorioUsuarioNeo.save(usuarioNeo)
        carritoService.vaciarCarrito(idUsuario)
    }

    /**
     * Verifica que el precio de una entrada no haya cambiado.
     * @param entrada Entrada a verificar
     * @throws RuntimeException si el precio cambió
     */
    private fun verificarPrecio(entrada: Entrada): Boolean {
        val showEnBase = repositorioShow.findDetailesById(entrada.showId)
        if (entrada.showEstado != showEnBase.showEstado) {
            throw RuntimeException("Ha ocurrido un cambio en el precio de una Entrada. Por favor intente de nuevo.")
        }
        return true
    }

    /**
     * Obtiene las entradas compradas por un usuario.
     * @param idUsuario ID del usuario
     * @return Lista de entradas compradas
     */
    fun obtenerEntradasCompradas(idUsuario: Long): List<Entrada> {
        val usuario = repositorioUsuarioComun.findById(idUsuario).get()
        return usuario.entradasCompradas
    }
}
