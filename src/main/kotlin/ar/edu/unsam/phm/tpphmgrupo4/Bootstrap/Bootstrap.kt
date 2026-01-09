package ar.edu.unsam.phm.tpphmgrupo4.Bootstrap

import ar.edu.unsam.phm.tpphmgrupo4.domain.*
import ar.edu.unsam.phm.tpphmgrupo4.domain.node.ShowNodo
import ar.edu.unsam.phm.tpphmgrupo4.domain.node.UsuarioNodo
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioEntradas
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioInstalacion
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioUsuarioAdmin
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioUsuarioComun
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.MongoRepository.RepositorioShow
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.NeoRepository.RepositorioShowNeo4j
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.NeoRepository.RepositorioUsuarioComunNeo4j
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.RedisRepository.RepositorioCarrito
import jakarta.transaction.Transactional
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime

@Service
class Bootstrap : InitializingBean {

    @Autowired
    lateinit var showRepository: RepositorioShow
    @Autowired
    lateinit var usuarioComunRepository: RepositorioUsuarioComun
    @Autowired
    lateinit var neo4jUsuarioComunRepository: RepositorioUsuarioComunNeo4j
    @Autowired
    lateinit var usuarioAdminRepository: RepositorioUsuarioAdmin
    @Autowired
    lateinit var instalacionRepository: RepositorioInstalacion
    @Autowired
    lateinit var entradaRepository: RepositorioEntradas
    @Autowired
    lateinit var carritoRepository: RepositorioCarrito
    @Autowired
    lateinit var showNodoRepository : RepositorioShowNeo4j

    lateinit var usuarioAlan: UsuarioComun
    lateinit var usuarioStefan: UsuarioComun
    lateinit var usuarioCeci: UsuarioComun
    lateinit var usuarioBraian: UsuarioComun
    lateinit var usuarioJuan: UsuarioComun
    lateinit var usuarioRodrigo : UsuarioComun
    lateinit var usuarioNico: UsuarioComun
    lateinit var usuarioGuille: UsuarioComun
    lateinit var usuarioFernando: UsuarioComun
    lateinit var usuarioJuli : UsuarioComun
    lateinit var usuarioJorge : UsuarioComun


    lateinit var carritoAlan: Carrito
    lateinit var carritoStefan: Carrito
    lateinit var carritoCeci: Carrito
    lateinit var carritoBraian: Carrito

    lateinit var usuarioRodri: UsuarioAdmin

    lateinit var teatroLunaPark: Teatro
    lateinit var estadioMasMonumental: Estadio
    lateinit var estadioAlmafitani: Estadio

    lateinit var show1: Show
    lateinit var show2: Show
    lateinit var show3: Show
    lateinit var show4: Show
    lateinit var show5: Show
    lateinit var show6 : Show
    lateinit var show7 : Show
    lateinit var show8: Show

    lateinit var usuarioAlanNeo : UsuarioNodo
    lateinit var usuarioCeciNeo : UsuarioNodo
    lateinit var usuarioBraianNeo : UsuarioNodo
    lateinit var usuarioRodrigoNeo : UsuarioNodo
    lateinit var usuarioStefanNeo : UsuarioNodo

    lateinit var show1Nodo: ShowNodo
    lateinit var show2Nodo: ShowNodo
    lateinit var show3Nodo: ShowNodo
    lateinit var show4Nodo: ShowNodo
    lateinit var show5Nodo: ShowNodo
    lateinit var show6Nodo : ShowNodo
    lateinit var show7Nodo : ShowNodo
    lateinit var show8Nodo: ShowNodo

    override fun afterPropertiesSet() {
        println("--------Inicializando---------")
        this.initBootstrap()
    }

    @Transactional
    fun initBootstrap() {
        this.deleteMemory()
        this.crearInstalaciones()
        this.crearShow()
        this.crearUsuariosYAdmin()
        this.crearEntradas()
        this.crearCarritos()
        this.compraDeEntradas()
        this.agregarAmigos()
        this.posteoComentarios()
    }
    fun deleteMemory() {
        usuarioComunRepository.deleteAll()
        usuarioAdminRepository.deleteAll()
        instalacionRepository.deleteAll()
        showRepository.deleteAll()
        entradaRepository.deleteAll()
        carritoRepository.deleteAll()
        neo4jUsuarioComunRepository.deleteAll()
        showNodoRepository.deleteAll()
    }

    fun crearUsuariosYAdmin() {
        usuarioAlan = UsuarioComun(
            "alan", "gomez", LocalDate.of(2000, 12, 10),
            "alanG", "password", "/src/assets/fotoperfil2.jpg"
        ).also {
            it.saldo = 500000.0
            it.dni = 99845383
        }

        usuarioStefan = UsuarioComun(
            "Stefan", "janzcuk", LocalDate.of(1997, 3, 21),
            "stefanJ", "password", "/src/assets/fotoperfil3.jpg"
        ).also {
            it.saldo = 600000.0
            it.dni = 54566313
        }

        usuarioBraian = UsuarioComun(
            "Braian", "Berardi", LocalDate.of(1994, 8, 12),
            "braianB", "password", "/src/assets/fotoperfil5.jpg"
        ).also {
            it.saldo = 789000.1
            it.dni = 13345332
        }

        usuarioCeci = UsuarioComun(
            "ceci", "dragonetti", LocalDate.of(1994, 6, 7),
            "ceci_d", "password", "/src/assets/fotoperfil4.jpg"
        ).also {
            it.saldo = 990110.0
            it.dni = 4356792
        }
        usuarioJuan = UsuarioComun(
            "Juan", "Contardo", LocalDate.of(1985, 3, 21),
            "juan", "password", ""
        ).also {
            it.saldo = 70000.0
            it.dni = 566313
        }
        usuarioFernando = UsuarioComun(
            "Fernando", "Dodino", LocalDate.of(1980, 5, 21),
            "dodino", "password", ""
        ).also {
            it.saldo = 8000.0
            it.dni = 2535523
        }
        usuarioJorge = UsuarioComun(
            "Jorge", "Lescano", LocalDate.of(1992, 7, 21),
            "jorge", "password", ""
        ).also {
            it.saldo = 9000.0
            it.dni = 5631322
        }
        usuarioNico = UsuarioComun(
            "Nicolas", "Viotti", LocalDate.of(1993, 3, 6),
            "viotti", "password", ""
        ).also {
            it.saldo = 9800.0
            it.dni = 566313040
        }

        usuarioRodrigo = UsuarioComun(
            "Rodrigo", "Pavon", LocalDate.of(1985, 3, 21),
            "rodriP", "password", ""
        ).also {
            it.saldo = 70000.0
            it.dni = 91220218
        }

        usuarioJuli= UsuarioComun(
            "Julian", "Mosquera", LocalDate.of(1985, 3, 21),
            "juliM", "password", ""
        ).also {
            it.saldo = 70000.0
            it.dni = 123000949
        }

        usuarioGuille= UsuarioComun(
            "Guillermo", "Bianchi", LocalDate.of(1985, 3, 21),
            "GuillermoB", "password", ""
        ).also {
            it.saldo = 70000.0
            it.dni = 34939320
        }


        usuarioRodri = UsuarioAdmin(
            "rodrigo", "pavon", LocalDate.of(1997, 7, 21),
            "romeAdm", "password", "/src/assets/fotoperfilAdmin.jpg"
        )

        usuarioCeciNeo = UsuarioNodo(usuarioCeci)
        usuarioAlanNeo = UsuarioNodo(usuarioAlan)
        usuarioBraianNeo = UsuarioNodo(usuarioBraian)
        usuarioStefanNeo = UsuarioNodo(usuarioStefan)
        usuarioRodrigoNeo = UsuarioNodo(usuarioRodrigo)

        usuarioComunRepository.save(usuarioAlan)
        usuarioComunRepository.save(usuarioBraian)
        usuarioComunRepository.save(usuarioCeci)
        usuarioComunRepository.save(usuarioStefan)
        usuarioComunRepository.save(usuarioRodrigo)
        usuarioComunRepository.save(usuarioGuille)
        usuarioComunRepository.save(usuarioJuan)
        usuarioComunRepository.save(usuarioNico)
        usuarioComunRepository.save(usuarioFernando)
        usuarioComunRepository.save(usuarioJorge)
        usuarioComunRepository.save(usuarioJuli)
        usuarioAdminRepository.save(usuarioRodri)

        neo4jUsuarioComunRepository.save(usuarioAlanNeo)
        neo4jUsuarioComunRepository.save(usuarioCeciNeo)
        neo4jUsuarioComunRepository.save(usuarioStefanNeo)
        neo4jUsuarioComunRepository.save(usuarioBraianNeo)
        neo4jUsuarioComunRepository.save(usuarioRodrigoNeo)

    }

    fun crearCarritos(){
        carritoAlan = Carrito(usuarioAlan.id.toInt())
        carritoCeci = Carrito(usuarioCeci.id.toInt())
        carritoRepository.saveAll(listOf(carritoAlan, carritoCeci))
    }

    fun crearInstalaciones() {
        teatroLunaPark = Teatro(
            true,
            2,
            3
        ).also {
            it.nombre = "Luna Park"
            it.longitud = "15.00.64"
            it.latitud = "19.00.24"
        }

        estadioMasMonumental = Estadio(
            2500.00,
            260,
            270,
            280
        ).also {
            it.nombre = "Estadio Mas Monumental"
            it.longitud="20.00.64"
            it.latitud = "20.00.24"

        }

        estadioAlmafitani = Estadio(
            1500.00,
            170,
            180,
            160
        ).also {
            it.nombre = "Estadio Almafitani"
            it.longitud="21.05.16"
            it.latitud = "43.60.05"

        }

        instalacionRepository.save(teatroLunaPark)
        instalacionRepository.save(estadioMasMonumental)
        instalacionRepository.save(estadioAlmafitani)
    }

    fun crearShow() {

        show1 = Show(
            "AC/DC",
            "Power Up Tour",
            "/src/assets/acdc.jpg",
            10000.00,
            teatroLunaPark.id!!,
            teatroLunaPark.nombre,
            teatroLunaPark.costoFijo,
            teatroLunaPark.tipoInstalacion,
            Estado.PrecioBase,
            mutableListOf()
        )
        show2 = Show(
            "Gorillaz",
            "The Gateway",
            "/src/assets/gorillaz.jpg",
            8000.00,
            estadioMasMonumental.id!!,
            estadioMasMonumental.nombre,
            estadioMasMonumental.costoFijo,
            estadioMasMonumental.tipoInstalacion,
            Estado.Megashow,
            mutableListOf()
        )

        show3 = Show(
            "SIAMÉS",
            "Home Tour",
            "/src/assets/siames.jpg",
            3500.00,
            teatroLunaPark.id!!,
            teatroLunaPark.nombre,
            teatroLunaPark.costoFijo,
            teatroLunaPark.tipoInstalacion,
            Estado.VentaPlena,
            mutableListOf()
        )
        show4= Show(
            "Ratones Paranoicos",
            "Ultima Cena Tour",
            "/src/assets/ratonesparanoicos.jpg",
             2000.00,
            estadioAlmafitani.id!!,
            estadioAlmafitani.nombre,
            estadioAlmafitani.costoFijo,
            estadioAlmafitani.tipoInstalacion,
            Estado.VentaPlena,
            mutableListOf()
        )
        show5 = Show(
            "El Bordo",
            "IRREAL",
            "/src/assets/elbordo.jpg",
            3000.00,
            estadioAlmafitani.id!!,
            estadioAlmafitani.nombre,
            estadioAlmafitani.costoFijo,
            estadioAlmafitani.tipoInstalacion,
            Estado.PrecioBase,
            mutableListOf()
        )

        show6= Show(
            "The Weeknd",
            "Dawn FM",
            "/src/assets/theWeeknd.jpg",
            3000.00,
            estadioMasMonumental.id!!,
            estadioMasMonumental.nombre,
            estadioMasMonumental.costoFijo,
            estadioMasMonumental.tipoInstalacion,
            Estado.Megashow,
            mutableListOf()
        )

        show7 = Show(
            "Kchiporros",
            "Los Ojos Rojos Tour",
            "/src/assets/Kchiporros.jpg",
            1500.00,
            estadioMasMonumental.id!!,
            estadioMasMonumental.nombre,
            estadioMasMonumental.costoFijo,
            estadioMasMonumental.tipoInstalacion,
            Estado.PrecioBase,
            mutableListOf()
        )

        show8 = Show(
            "Ke Personajes",
            "Una mas para el cuaderno",
            "/src/assets/ke-personajes.png",
            5000.00,
            teatroLunaPark.id!!,
            teatroLunaPark.nombre,
            teatroLunaPark.costoFijo,
            teatroLunaPark.tipoInstalacion,
            Estado.PrecioBase,
            mutableListOf()
        )

        show1.agregarFuncion(
                LocalDate.now().plusMonths(1),
                LocalTime.of(21, 30, 40),
                instalacionRepository.findById(show1.instalacionId).get()
            )
            show1.agregarFuncion(
                LocalDate.now().plusMonths(1).plusDays(1),
                LocalTime.of(21, 30, 40),
                instalacionRepository.findById(show1.instalacionId).get()
            )
            show1.agregarFuncion(
                LocalDate.now().plusMonths(1).plusDays(3),
                LocalTime.of(21, 30, 40),
                instalacionRepository.findById(show1.instalacionId).get()
            )
            show2.agregarFuncion(
                LocalDate.now().plusMonths(2),
                LocalTime.of(21, 30, 40),
                instalacionRepository.findById(show2.instalacionId).get()
            )
        show2.agregarFuncion(
            LocalDate.of(2027, 7, 24),
            LocalTime.of(21, 30, 40),
            instalacionRepository.findById(show2.instalacionId).get()
        )
        show2.agregarFuncion(
            LocalDate.of(2027, 7, 26),
            LocalTime.of(21, 30, 40),
            instalacionRepository.findById(show2.instalacionId).get()
        )
        show3.agregarFuncion(
            LocalDate.of(2023, 1, 15),
            LocalTime.of(21, 30, 40),
            instalacionRepository.findById(show3.instalacionId).get()
        )
        show4.agregarFuncion(
            LocalDate.of(2028, 9, 14),
            LocalTime.of(21, 30, 40),
            instalacionRepository.findById(show4.instalacionId).get()
        )
        show5.agregarFuncion(
            LocalDate.of(2028, 8, 30),
            LocalTime.of(21, 30, 40),
            instalacionRepository.findById(show5.instalacionId).get()
        )
        show6.agregarFuncion(
            LocalDate.of(2026, 10, 18),
            LocalTime.of(21, 30, 40),
            instalacionRepository.findById(show6.instalacionId).get()
        )
        show7.agregarFuncion(
            LocalDate.of(2028, 10, 9),
            LocalTime.of(21, 30, 0),
            instalacionRepository.findById(show7.instalacionId).get()
        )
        show8.agregarFuncion(
            LocalDate.of(2024, 2, 2),
            LocalTime.of(21, 30, 0),
            instalacionRepository.findById(show8.instalacionId).get()
        )
        showRepository.saveAll(listOf(show1, show2, show3, show4, show5, show6,show7,show8))

        show1Nodo = ShowNodo(show1)
        show2Nodo = ShowNodo(show2)
        show3Nodo = ShowNodo(show3)
        show4Nodo = ShowNodo(show4)
        show5Nodo =  ShowNodo(show5)
        show6Nodo = ShowNodo(show6)
        show7Nodo = ShowNodo(show7)
        show8Nodo = ShowNodo(show8)

        showNodoRepository.saveAll(listOf(show1Nodo, show2Nodo, show3Nodo, show4Nodo, show5Nodo, show6Nodo, show7Nodo, show8Nodo))
    }

    fun crearEntradas() {
        val listaShows = listOf(show1, show2, show3, show4, show5, show6, show7,show8)
        listaShows.forEach{show ->
            show.funciones.forEach { funcion ->
                for (i in 1..3) {
                    entradaRepository.save(
                        Entrada(
                            instalacionRepository.findById(show.instalacionId).get().ubicacionMenor(),
                            funcion.fecha,
                            funcion.precioBaseEntrada,
                            funcion.showEstado,
                            funcion.id,
                            show.id
                        )
                    )
                    entradaRepository.save(
                        Entrada(
                            instalacionRepository.findById(show.instalacionId).get().ubicacionMayor(),
                            funcion.fecha,
                            funcion.precioBaseEntrada,
                            funcion.showEstado,
                            funcion.id,
                            show.id
                        )
                    )
                }
            }
        }
        val showsSinEntradas = listOf(show4, show5, show6, show7)
        showsSinEntradas.forEach { show ->
            show.funciones.forEach { funcion -> funcion.funcionAgotada() }
        }
        showRepository.saveAll(showsSinEntradas)
    }

    fun entradasDeFuncion(idShow: String, idFuncion: String): List<Entrada> {
        return entradaRepository.findEntradasByShowIdAndFuncionId(idShow, idFuncion)
    }

    fun compraDeEntradas() {
        val usuarioAlanRepo = usuarioComunRepository.findById(usuarioAlan.id).orElseThrow() as UsuarioComun
        val usuarioCeciRepo = usuarioComunRepository.findById(usuarioCeci.id).orElseThrow() as UsuarioComun
        val carritoAlanRepo = carritoRepository.findById(usuarioAlan.id).orElseThrow() as Carrito
        val carritoCeciRepo = carritoRepository.findById(usuarioCeci.id).orElseThrow() as Carrito

        show1.funciones[0].let { funcion ->
            val entradas = entradasDeFuncion(show1.id, funcion.id)
            carritoAlanRepo.agregarEntradasACarrito(entradas.subList(1,2))
            carritoAlanRepo.agregarEntradasACarrito(entradas.subList(3,4))
            val vendidas = entradas.subList(1,4)
            vendidas.forEach { it.vender() }
            entradaRepository.saveAll(vendidas)
            entradas.forEach { entrada ->
                usuarioAlanNeo.agregarEntrada(show1Nodo, entrada)
            }
        }

        show1.funciones[1].let { funcion ->
            val entradas = entradasDeFuncion(show1.id, funcion.id)
            carritoAlanRepo.agregarEntradasACarrito(listOf(entradas[1]))
            carritoAlanRepo.agregarEntradasACarrito(listOf(entradas[2]))
            val vendidas = entradas.subList(1,2)
            vendidas.forEach { it.vender() }
            entradaRepository.saveAll(vendidas)
            entradas.forEach { entrada ->
            usuarioAlanNeo.agregarEntrada(show1Nodo, entrada)
            }

        }


        show2.funciones[0].let { funcion ->
            val entradas = entradasDeFuncion(show2.id, funcion.id)
            carritoCeciRepo.agregarEntradasACarrito(listOf(entradas[1]))
            entradas[1].vender()
            entradaRepository.save(entradas[1])
            entradas.forEach { entrada ->
                usuarioCeciNeo.agregarEntrada(show2Nodo, entrada)
            }
        }

        show3.funciones[0].let { funcion ->
            val entradas = entradasDeFuncion(show3.id, funcion.id)
            carritoAlanRepo.agregarEntradasACarrito(listOf(entradas[1]))
            carritoCeciRepo.agregarEntradasACarrito(entradas.subList(2,3))
            val vendidas = entradas.subList(1,3)
            vendidas.forEach { it.vender() }
            entradaRepository.saveAll(vendidas)
            entradas.forEach { entrada ->
                usuarioCeciNeo.agregarEntrada(show3Nodo, entrada)
                usuarioAlanNeo.agregarEntrada(show3Nodo, entrada)
            }
        }

        show8.funciones[0].let { funcion ->
            val entradas = entradasDeFuncion(show8.id, funcion.id)
            carritoCeciRepo.agregarEntradasACarrito(listOf(entradas[1]))
            entradas[1].vender()
            entradaRepository.save(entradas[1])
            entradas.forEach { entrada ->
                usuarioCeciNeo.agregarEntrada(show8Nodo, entrada)
                usuarioAlanNeo.agregarEntrada(show8Nodo, entrada)
            }
        }

        carritoAlanRepo.comprarEntradas(usuarioAlanRepo)
        usuarioComunRepository.save(usuarioAlanRepo)
        neo4jUsuarioComunRepository.save(usuarioAlanNeo)




        carritoCeciRepo.comprarEntradas(usuarioCeciRepo)
        usuarioComunRepository.save(usuarioCeciRepo)
        neo4jUsuarioComunRepository.save(usuarioCeciNeo)
    }

    fun agregarAmigos() {
        val usuarioAlanRepo = usuarioComunRepository.findById(usuarioAlan.id).orElseThrow() as UsuarioComun
        val usuarioCeciRepo = usuarioComunRepository.findById(usuarioCeci.id).orElseThrow() as UsuarioComun
        val usuarioBraianRepo = usuarioComunRepository.findById(usuarioBraian.id).orElseThrow() as UsuarioComun
        val usuarioStefanRepo = usuarioComunRepository.findById(usuarioStefan.id).orElseThrow() as UsuarioComun

        usuarioAlanRepo.agregarAmigo(usuarioCeci.id.toInt())
        usuarioAlanRepo.agregarAmigo(usuarioBraian.id.toInt())
        usuarioCeciRepo.agregarAmigo(usuarioAlan.id.toInt())
        usuarioCeciRepo.agregarAmigo(usuarioBraian.id.toInt())
        usuarioCeciRepo.agregarAmigo(usuarioStefan.id.toInt())
        usuarioBraianRepo.agregarAmigo(usuarioAlan.id.toInt())
        usuarioBraianRepo.agregarAmigo(usuarioCeci.id.toInt())
        usuarioStefanRepo.agregarAmigo(usuarioCeci.id.toInt())

        usuarioComunRepository.saveAll(listOf(usuarioAlanRepo, usuarioCeciRepo, usuarioBraianRepo, usuarioStefanRepo))

        usuarioAlanNeo.agregarAmigo(usuarioBraianNeo)
        usuarioAlanNeo.agregarAmigo(usuarioStefanNeo)

        usuarioStefanNeo.agregarAmigo(usuarioAlanNeo)
        usuarioStefanNeo.agregarAmigo(usuarioCeciNeo)

        usuarioCeciNeo.agregarAmigo(usuarioStefanNeo)
        usuarioCeciNeo.agregarAmigo(usuarioRodrigoNeo)

        usuarioRodrigoNeo.agregarAmigo(usuarioCeciNeo)
        usuarioRodrigoNeo.agregarAmigo(usuarioBraianNeo)

        usuarioBraianNeo.agregarAmigo(usuarioAlanNeo)
        usuarioBraianNeo.agregarAmigo(usuarioRodrigoNeo)

        neo4jUsuarioComunRepository.saveAll(listOf(usuarioAlanNeo,
            usuarioCeciNeo, usuarioBraianNeo,
            usuarioStefanNeo, usuarioRodrigoNeo))

    }

    fun posteoComentarios() {
        val usuarioAlanRepo = usuarioComunRepository.findById(usuarioAlan.id).orElseThrow() as UsuarioComun
        val usuarioCeciRepo = usuarioComunRepository.findById(usuarioCeci.id).orElseThrow() as UsuarioComun

        usuarioCeciRepo.dejarComentario(show3, "una experiecia innolvidable", 4.75f)
        usuarioAlanRepo.dejarComentario(show3, "Volvi a sentir esa vibra de ninio", 5f)
        show3.actualizarComentarios(4.88, 2)
        showRepository.save(show3)
        usuarioComunRepository.save(usuarioAlanRepo)
        usuarioComunRepository.save(usuarioCeciRepo)
    }

}