package ar.edu.unsam.phm.tpphmgrupo4.service

import ar.edu.unsam.phm.tpphmgrupo4.DTO.*
import ar.edu.unsam.phm.tpphmgrupo4.Exceptions.UnathorizedUser
import ar.edu.unsam.phm.tpphmgrupo4.domain.*
import ar.edu.unsam.phm.tpphmgrupo4.domain.node.UsuarioNodo
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.*
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.MongoRepository.RepositorioShow
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.NeoRepository.RepositorioShowNeo4j
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.NeoRepository.RepositorioUsuarioComunNeo4j
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.RedisRepository.RepositorioCarrito
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ServiceUsuario {
    @Autowired
    lateinit var repositorioUsuarioAdmin: RepositorioUsuarioAdmin
    @Autowired
    lateinit var repositorioUsuarioComun: RepositorioUsuarioComun
    @Autowired
    lateinit var repositorioShow: RepositorioShow
    @Autowired
    lateinit var repositorioComentarios: RepositorioComentarios
    @Autowired
    lateinit var repositorioEntrada: RepositorioEntradas
    @Autowired
    lateinit var repositorioInstalacion: RepositorioInstalacion
    @Autowired
    lateinit var repositorioCarrito: RepositorioCarrito
    @Autowired
    lateinit var repositorioUsuarioNeo : RepositorioUsuarioComunNeo4j
    @Autowired
    lateinit var repositorioShowNeo: RepositorioShowNeo4j

    fun getUserByID(id: Long): UsuarioComun {
        return repositorioUsuarioComun.findById(id.toLong()).get()
    }

    fun getUserNeoByName(nombre: String): UsuarioNodo {
        return repositorioUsuarioNeo.findUsuarioNodoByUsername(nombre)
    }

    fun getCarritoById(idUsuario: Long): Carrito {
        return repositorioCarrito.findById(idUsuario)
            .orElseGet {
                val usuario = repositorioUsuarioComun.findById(idUsuario)
                    .orElseThrow { RuntimeException("Usuario no encontrado") }
                val carritoNuevo = Carrito(usuario.id.toInt(), mutableListOf())
                repositorioCarrito.save(carritoNuevo)
            }
    }


    @Transactional(Transactional.TxType.NEVER)
    fun loginUsuario(user: LoginDTO): Any {
        val usuarioComun = repositorioUsuarioComun.findByUsernameAndPassword(user.username, user.password)
        val usuarioAdmin = repositorioUsuarioAdmin.findByUsernameAndPassword(user.username, user.password)

        return if (usuarioComun.isPresent) {
            UsuarioDataDTO.fromUserDataPostLogin(usuarioComun.get())
        } else if (usuarioAdmin.isPresent) {
            AdminDTO.fromAdminDTO(usuarioAdmin.get())
        } else {
            throw UnathorizedUser("Usuario y/o contraseña incorrecta")
        }
    }

    @Transactional(Transactional.TxType.NEVER)
    fun getDataUserByID(id: Int): UsuarioDTO {
        return UsuarioDTO.fromUsuario(repositorioUsuarioComun.findById(id.toLong()).get())
    }

    @Transactional(Transactional.TxType.NEVER)
    fun editarDatos(idUsuario: Int, nombre: String, apellido: String) {
        val usuario = repositorioUsuarioComun.findEditById(idUsuario.toLong()).get()
        usuario.cambiarNombres(nombre, apellido)
        repositorioUsuarioComun.save(usuario)
    }

    fun sumarCredito(idUsuario: Long, credito: Double) {
        val usuario = getUserByID(idUsuario)
        usuario.aumentarSaldo(credito)
        repositorioUsuarioComun.save(usuario)
    }

    @Transactional(Transactional.TxType.NEVER)
    fun listaAmigos(idUsuario: Long): List<UsuarioComun> {
        val usuario = getUserByID(idUsuario)
        return usuario.amigos.map { repositorioUsuarioComun.findById(it.toLong()).get() }
    }

    fun listaComentarios(idUsuario: Int): List<Comentario> {
        return repositorioUsuarioComun.findComentariosByUserId(idUsuario.toLong())
    }


    fun comprarEntradas(idUsuario: Long) {
        val usuario = getUserByID(idUsuario)
        val usuarioNeo = getUserNeoByName(usuario.username)
        val carrito = getCarritoById(idUsuario)
        val setShows = mutableSetOf<Show>()
        carrito.items.forEach {
            verificarPrecio(it)
            val show = repositorioShow.findById(it.showId).get()
            setShows.add(show)
        }
        carrito.comprarEntradas(usuario)
        setShows.forEach {
            val funciones = it.funciones
            val showNeo = repositorioShowNeo.findShowNodoByShowId(it.id)
            funciones.forEach { funcion ->
                val entradasFuncion = repositorioEntrada.findEntradaByFuncionId(funcion.id)
                if (entradasFuncion.all { entrada -> entrada.estaVendida }) funcion.funcionAgotada()
                entradasFuncion.forEach { entrada -> usuarioNeo.agregarEntrada(showNeo, entrada) }
            }
        }
        repositorioShow.saveAll(setShows)
        repositorioEntrada.saveAll(carrito.items)
        repositorioUsuarioComun.save(usuario)
        repositorioCarrito.save(carrito)
    }

    fun verificarPrecio(entrada: Entrada): Boolean{
        val showEnBase = repositorioShow.findDetailesById(entrada.showId)
        if( entrada.showEstado == showEnBase.showEstado ){
            return true
        } else{
            throw RuntimeException("Ha ocurrido un cambio en el precio de una Entrada. Por favor intente de nuevo.")
        }
    }

    fun traerComentarios(showId: String): List<Comentario> {
        return repositorioUsuarioComun.findComentariosDeShow(showId)
    }

    @Transactional(Transactional.TxType.NEVER)
    fun entradasCompradas(idUsuario: Long): List<CarritoDTO> {
        val usuario = getUserByID(idUsuario)
        return usuario.entradasCompradas.map { entrada: Entrada ->
            val show = repositorioShow.findById(entrada.showId).get()
            val instalacion = repositorioInstalacion.findById(show.instalacionId).get()
            CarritoDTO.toEntradaCarritoDTO(
                entrada,
                show,
                instalacion,
                getCarritoById(idUsuario),
                amigosQueVanAShow(idUsuario, entrada.showId),
            )
        }
    }

    fun amigosQueVanAShow(idUsuario: Long, idShow: String): List<UsuarioAmigosDTO> {
        val usuario = getUserByID(idUsuario)
        val amigos = usuario.amigos.map { repositorioUsuarioComun.findById(it.toLong()).get() }
        val amigosFiltrados = amigos.filter { it.listaIdShows().contains(idShow) }
        return amigosFiltrados.map { UsuarioAmigosDTO.fromAmigosDTO(it) }
    }

    fun agregarAmigo(idUsuario: Long, idAmigo: Int) {
        val usuario = getUserByID(idUsuario)
        usuario.agregarAmigo(idAmigo)
        repositorioUsuarioComun.save(usuario)

        val usuarioNeo = getUserNeoByName(usuario.username)
        val amigoNeo = getUserNeoByName(getUserByID(idAmigo.toLong()).username)
        usuarioNeo.agregarAmigo(amigoNeo)
        repositorioUsuarioNeo.save(usuarioNeo)
    }

    fun quitarAmigo(idUsuario: Long, idAmigo: Int) {
        val usuario = getUserByID(idUsuario)
        usuario.quitarAmigo(idAmigo)
        repositorioUsuarioComun.save(usuario)

        val usuarioNeo = getUserNeoByName(usuario.username)
        val amigoNeo = getUserNeoByName(getUserByID(idAmigo.toLong()).username)
        usuarioNeo.quitarAmigo(amigoNeo)
        repositorioUsuarioNeo.save(usuarioNeo)
    }

    fun actualizarPuntaje(idShow: String) {
        val show = repositorioShow.findById(idShow).get()
        val comentarios = repositorioUsuarioComun.findComentariosDeShow(idShow)
        val puntaje = repositorioComentarios.obtenerPromedioPuntuacionPorIdShow(idShow)
        show.actualizarComentarios(puntaje, comentarios.size)
        repositorioShow.save(show)
    }

    fun borrarComentario(idUsuario: Long, idShow: String) {
        val usuario = getUserByID(idUsuario)
        usuario.borrarComentario(idShow)
        repositorioUsuarioComun.save(usuario)
        actualizarPuntaje(idShow)
    }

    fun dejarComentario(idUsuario: Long, idShow: String, comentario: String, puntuacion: Float) {
        val usuario = getUserByID(idUsuario)
        val show = repositorioShow.findById(idShow).get()
        usuario.dejarComentario(show, comentario, puntuacion)
        repositorioUsuarioComun.save(usuario)
        actualizarPuntaje(idShow)
    }

    fun agregarCarrito(idUsuario: Long, idShow: String, idFuncion: Int, cantidad: Int, ubi: Ubicacion) {
        require(cantidad > 0) { "La cantidad debe ser mayor que cero" }
        val carrito = getCarritoById(idUsuario)
        val show = repositorioShow.findById(idShow).get()
        val funcion = show.funciones[idFuncion]
        val entradasDisponibles = repositorioEntrada.findEntradasByShowIdAndFuncionId(idShow, funcion.id).filter {
            !it.estaVendida && it.ubicacion == ubi
        }
        val entradasCarrito = mutableListOf<Entrada>()
        if (entradasDisponibles.size >= cantidad) {
            for (i in 1..cantidad) {
                val entrada = entradasDisponibles[i - 1]
                entradasCarrito.add(entrada)
            }
        } else {
            throw RuntimeException("No hay entradas disponibles")
        }
        carrito.agregarEntradasACarrito(entradasCarrito)
        entradasCarrito.forEach { it.vender() }
        repositorioCarrito.save(carrito)
        repositorioEntrada.saveAll(entradasCarrito)
    }

    fun carritoCompra(idUsuario: Long): List<Entrada> {
        val carrito = getCarritoById(idUsuario)
        return carrito.items
    }

    fun vaciarCarrito(idUsuario: Long) {
        val carrito = getCarritoById(idUsuario)
        carrito.items.clear()
        repositorioCarrito.save(carrito)
    }
    fun amiguesSugeridos(username: String): List<UsuarioNodo> {
        return repositorioUsuarioNeo.amiguesSugeridos(username)
    }
}