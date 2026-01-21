package ar.edu.unsam.phm.tpphmgrupo4.Controller

import ar.edu.unsam.phm.tpphmgrupo4.DTO.*
import ar.edu.unsam.phm.tpphmgrupo4.domain.*
import ar.edu.unsam.phm.tpphmgrupo4.domain.node.UsuarioNodo
import ar.edu.unsam.phm.tpphmgrupo4.service.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Controller refactorizado siguiendo principios SOLID.
 * 
 * Principios aplicados:
 * - SRP: Cada endpoint tiene una responsabilidad clara
 * - DIP: Depende de abstracciones (servicios inyectados)
 * - ISP: Usa múltiples servicios especializados en lugar de uno monolítico
 * 
 * Mejoras aplicadas:
 * - Separación de responsabilidades entre múltiples servicios
 * - Constructor injection para mejor testabilidad
 * - Cada servicio tiene una responsabilidad única
 */
@RestController
@CrossOrigin("*")
class UsuarioController(
    private val serviceUsuario: ServiceUsuario,
    private val authenticationService: AuthenticationService,
    private val carritoService: CarritoService,
    private val compraService: CompraService,
    private val amistadService: AmistadService,
    private val comentarioService: ComentarioService,
    private val serviceShow: ServiceShow
) {

    @PostMapping("/usuario-logueado")
    fun traerUsuarioLogin(@RequestBody user: LoginDTO): Any {
       return authenticationService.loginUsuario(user)
    }

    @GetMapping("/user/{idUser}")
    fun getUsuarioComunByID(@PathVariable idUser: Int): Any{
        return serviceUsuario.obtenerDatosUsuario(idUser)
    }

    @GetMapping("/carrito/{idUser}")
    fun getCarrito (@PathVariable idUser : Long) : MutableList<CarritoDTO> {
        return carritoService.obtenerEntradasCarrito(idUser).map { entrada: Entrada ->
            val show = serviceShow.findShowById(entrada.showId)
            val amigosQueVan = amistadService.amigosQueVanAShow(idUser, entrada.showId)
                .map { UsuarioAmigosDTO.fromAmigosDTO(it) }
            CarritoDTO.toEntradaCarritoDTO(
                entrada,
                show,
                serviceShow.findInstalacionById(show.instalacionId),
                carritoService.getCarritoById(idUser),
                    amigosQueVan,
            )
        }.toMutableList()
    }

    @PostMapping("/agregar-carrito/{idUser}")
    fun agregarAlCarrito(@PathVariable idUser: Long, @RequestBody carrito: CarritoGetDTO) {
        carritoService.agregarAlCarrito(
            idUser, 
            carrito.idShow, 
            carrito.idFuncion!!, 
            carrito.cantidad!!, 
            carrito.ubicacion
        )
    }

    @PatchMapping("/editar-datos-usuario/{idUser}")
    fun editarDatosUsuario (@PathVariable idUser : Int, @RequestBody user: UsuarioEditarDTO) {
        serviceUsuario.actualizarDatosPersonales(idUser, user.nombre, user.apellido)
    }

    @PatchMapping("/sumar-credito/{idUser}/{credito}")
    fun sumarCredito (@PathVariable idUser : Long, @PathVariable credito : Double) {
        serviceUsuario.agregarCredito(idUser, credito)
    }

    @PutMapping("/agregar-amigo/{idUser}/{idAmigo}")
    fun agregarAmigo (@PathVariable idUser : Long, @PathVariable idAmigo : Int) {
        amistadService.agregarAmigo(idUser, idAmigo)
    }

    @PutMapping("/quitar-amigo/{idUser}/{idAmigo}")
    fun quitarAmigo (@PathVariable idUser : Long, @PathVariable idAmigo : Int) {
        amistadService.quitarAmigo(idUser, idAmigo)
    }

    @GetMapping("/entradas-compradas/{idUser}")
    fun getEntradasCompradas (@PathVariable idUser : Long) : MutableList<CarritoDTO> {
        return compraService.obtenerEntradasCompradas(idUser).map { entrada: Entrada ->
            val show = serviceShow.findShowById(entrada.showId)
            val instalacion = serviceShow.findInstalacionById(show.instalacionId)
            val amigosQueVan = amistadService.amigosQueVanAShow(idUser, entrada.showId)
                .map { UsuarioAmigosDTO.fromAmigosDTO(it) }
            CarritoDTO.toEntradaCarritoDTO(
                entrada,
                show,
                instalacion,
                carritoService.getCarritoById(idUser),
                amigosQueVan,
            )
        }.toMutableList()
    }

    @PostMapping("/comprar-entradas/{idUser}")
    fun comprarEntradas(@PathVariable idUser: Long) {
        compraService.comprarEntradas(idUser)
    }

    @GetMapping("/lista-amigos/{idUser}")
    fun getAmigos (@PathVariable idUser : Long) : MutableList<UsuarioAmigosDTO> {
        return amistadService.listaAmigos(idUser).map { amigo: Usuario -> 
            UsuarioAmigosDTO.fromAmigosDTO(amigo as UsuarioComun)
        }.toMutableList()
    }

    @GetMapping("/lista-comentarios/{idUser}")
    fun getComentarios (@PathVariable idUser : Int) : MutableList<ComentarioDTO> {
        return comentarioService.listaComentarios(idUser).map { comentario: Comentario -> 
            ComentarioDTO.fromComentario(comentario)
        }.toMutableList()
    }

    @DeleteMapping("/carrito-vacio/{idUser}")
    fun removeCarrito(@PathVariable idUser: Long) {
        return carritoService.vaciarCarrito(idUser)
    }

    @PostMapping("/dejar-comentario/{idUser}/{idShow}/{idEntrada}")
    fun dejarComentario(
        @PathVariable idUser: Long, 
        @PathVariable idShow: String, 
        @PathVariable idEntrada: Int, 
        @RequestBody comentario: ComentarioNuevoDTO
    ) {
        return comentarioService.dejarComentario(idUser, idShow, comentario.contenido, comentario.puntuacion!!)
    }

    @DeleteMapping("/borrar-comentario/{idUser}/{idShow}")
    fun borrarComentario(@PathVariable idUser: Long, @PathVariable idShow: String) {
        return comentarioService.borrarComentario(idUser, idShow)
    }

    @GetMapping("/sugerir-amigues/{username}")
    fun amiguesSugeridos(@PathVariable username: String): List<UsuarioNodo>{
        return amistadService.amiguesSugeridos(username)
    }
}
