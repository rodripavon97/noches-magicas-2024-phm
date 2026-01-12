package ar.edu.unsam.phm.tpphmgrupo4.Controller

import ar.edu.unsam.phm.tpphmgrupo4.DTO.*
import ar.edu.unsam.phm.tpphmgrupo4.domain.*
import ar.edu.unsam.phm.tpphmgrupo4.domain.node.UsuarioNodo
import ar.edu.unsam.phm.tpphmgrupo4.service.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Controller refactorizado siguiendo principios SOLID.
 * Ahora utiliza servicios especializados en lugar de un único ServiceUsuario monolítico.
 * 
 * Mejoras aplicadas:
 * - Separación de responsabilidades entre múltiples servicios
 * - Cada servicio tiene una responsabilidad única (SRP)
 * - Eliminada dependencia de ServiceShow (mejor separación de concerns)
 */
@RestController
@CrossOrigin("*")
class UsuarioController {
    @Autowired 
    lateinit var serviceUsuario: ServiceUsuario
    
    @Autowired 
    lateinit var authenticationService: AuthenticationService
    
    @Autowired 
    lateinit var carritoService: CarritoService
    
    @Autowired 
    lateinit var compraService: CompraService
    
    @Autowired 
    lateinit var amistadService: AmistadService
    
    @Autowired 
    lateinit var comentarioService: ComentarioService
    
    @Autowired 
    lateinit var serviceShow: ServiceShow

    @PostMapping("/usuario-logueado")
    fun traerUsuarioLogin(@RequestBody user: LoginDTO): Any {
       return authenticationService.loginUsuario(user)
    }

    @GetMapping("/user/{idUser}")
    fun getUsuarioComunByID(@PathVariable idUser: Int): Any{
        return serviceUsuario.getDataUserByID(idUser)
    }

    @GetMapping("/carrito/{idUser}")
    fun getCarrito (@PathVariable idUser : Long) : MutableList<CarritoDTO> {
        return carritoService.obtenerEntradasCarrito(idUser).map { entrada: Entrada ->
            val show = serviceShow.getShowByID(entrada.showId)
            CarritoDTO.toEntradaCarritoDTO(
                entrada,
                show,
                serviceShow.getInstalacionByID(show.instalacionId),
                carritoService.getCarritoById(idUser),
                amistadService.amigosQueVanAShow(idUser, entrada.showId),
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
        serviceUsuario.editarDatos(idUser, user.nombre, user.apellido)
    }

    @PatchMapping("/sumar-credito/{idUser}/{credito}")
    fun sumarCredito (@PathVariable idUser : Long, @PathVariable credito : Double) {
        serviceUsuario.sumarCredito(idUser, credito)
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
            val show = serviceShow.getShowByID(entrada.showId)
            val instalacion = serviceShow.getInstalacionByID(show.instalacionId)
            CarritoDTO.toEntradaCarritoDTO(
                entrada,
                show,
                instalacion,
                carritoService.getCarritoById(idUser),
                amistadService.amigosQueVanAShow(idUser, entrada.showId),
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
