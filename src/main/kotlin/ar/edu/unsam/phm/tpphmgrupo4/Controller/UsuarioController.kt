package ar.edu.unsam.phm.tpphmgrupo4.Controller

import ar.edu.unsam.phm.tpphmgrupo4.DTO.*
import ar.edu.unsam.phm.tpphmgrupo4.domain.*
import ar.edu.unsam.phm.tpphmgrupo4.domain.node.UsuarioNodo
import ar.edu.unsam.phm.tpphmgrupo4.service.ServiceShow
import ar.edu.unsam.phm.tpphmgrupo4.service.ServiceUsuario

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin("*")
class UsuarioController {
    @Autowired lateinit var usuarioService : ServiceUsuario
    @Autowired lateinit var serviceShow : ServiceShow

    @PostMapping("/usuario-logueado")
    fun traerUsuarioLogin(@RequestBody user: LoginDTO): Any {
       return usuarioService.loginUsuario(user)
    }

    @GetMapping("/user/{idUser}")
    fun getUsuarioComunByID(@PathVariable idUser: Int): Any{
        return usuarioService.getDataUserByID(idUser)
    }

    @GetMapping("/carrito/{idUser}")
    fun getCarrito (@PathVariable idUser : Long) : MutableList<CarritoDTO> {
        return usuarioService.carritoCompra(idUser).map {
            entrada : Entrada ->
                val show = serviceShow.getShowByID(entrada.showId)
                CarritoDTO.toEntradaCarritoDTO(
                    entrada,
                    show,
                    serviceShow.getInstalacionByID(show.instalacionId),
                    usuarioService.getCarritoById(idUser),
                    usuarioService.amigosQueVanAShow(idUser, entrada.showId),
                )
        }.toMutableList()
    }


    @PostMapping("/agregar-carrito/{idUser}")
    fun agregarAlCarrito(@PathVariable idUser: Long, @RequestBody carrito: CarritoGetDTO) {
        usuarioService.agregarCarrito(idUser, carrito.idShow, carrito.idFuncion!!, carrito.cantidad!!, carrito.ubicacion)
    }


    @PatchMapping("/editar-datos-usuario/{idUser}")
    fun editarDatosUsuario (@PathVariable idUser : Int, @RequestBody user: UsuarioEditarDTO) {
        usuarioService.editarDatos(idUser, user.nombre, user.apellido)
    }


    @PatchMapping("/sumar-credito/{idUser}/{credito}")
    fun sumarCredito (@PathVariable idUser : Long, @PathVariable credito : Double) {
        usuarioService.sumarCredito(idUser, credito)
    }

    @PutMapping("/agregar-amigo/{idUser}/{idAmigo}")
    fun agregarAmigo (@PathVariable idUser : Long, @PathVariable idAmigo : Int) {
        usuarioService.agregarAmigo(idUser, idAmigo)
    }

    @PutMapping("/quitar-amigo/{idUser}/{idAmigo}")
    fun quitarAmigo (@PathVariable idUser : Long, @PathVariable idAmigo : Int) {
        usuarioService.quitarAmigo(idUser, idAmigo)
    }

    @GetMapping("/entradas-compradas/{idUser}")
    fun getEntradasCompradas (@PathVariable idUser : Long) : MutableList<CarritoDTO> {
        return usuarioService.entradasCompradas(idUser).toMutableList()
    }

    @PostMapping("/comprar-entradas/{idUser}")
    fun comprarEntradas(@PathVariable idUser: Long) {
        usuarioService.comprarEntradas(idUser)
    }

    @GetMapping("/lista-amigos/{idUser}")
    fun getAmigos (@PathVariable idUser : Long) : MutableList<UsuarioAmigosDTO> {
        return usuarioService.listaAmigos(idUser).map { amigo : Usuario -> UsuarioAmigosDTO.fromAmigosDTO(amigo as UsuarioComun)}.toMutableList()
    }

    @GetMapping("/lista-comentarios/{idUser}")
    fun getComentarios (@PathVariable idUser : Int) : MutableList<ComentarioDTO> {
        return usuarioService.listaComentarios(idUser).map { comentario : Comentario -> ComentarioDTO.fromComentario(comentario)}.toMutableList()
    }

    @DeleteMapping("/carrito-vacio/{idUser}")
    fun removeCarrito(@PathVariable idUser: Long) {
        return usuarioService.vaciarCarrito(idUser)
    }

    @PostMapping("/dejar-comentario/{idUser}/{idShow}/{idEntrada}")
    fun dejarComentario(@PathVariable idUser: Long, @PathVariable idShow: String, @PathVariable idEntrada: Int, @RequestBody comentario : ComentarioNuevoDTO) {
        return usuarioService.dejarComentario(idUser, idShow, comentario.contenido, comentario.puntuacion!!)
    }

    @DeleteMapping("/borrar-comentario/{idUser}/{idShow}")
    fun borrarComentario(@PathVariable idUser: Long, @PathVariable idShow: String) {
        return usuarioService.borrarComentario(idUser, idShow)
    }

    @GetMapping("/sugerir-amigues/{username}")
    fun amiguesSugeridos(@PathVariable username: String): List<UsuarioNodo>{
        return usuarioService.amiguesSugeridos(username)
    }
}