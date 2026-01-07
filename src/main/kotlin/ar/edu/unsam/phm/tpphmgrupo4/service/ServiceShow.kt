package ar.edu.unsam.phm.tpphmgrupo4.service

import ar.edu.unsam.phm.tpphmgrupo4.DTO.*
import ar.edu.unsam.phm.tpphmgrupo4.Exceptions.UnathorizedUser
import ar.edu.unsam.phm.tpphmgrupo4.domain.*
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioEntradas
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioInstalacion
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioUsuarioAdmin
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioUsuarioComun
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.MongoRepository.RepositorioLogs
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.MongoRepository.RepositorioShow
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.NeoRepository.RepositorioShowNeo4j
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Service
class ServiceShow {
    @Autowired
    lateinit var showRepository: RepositorioShow

    @Autowired
    lateinit var usuarioComunRepository: RepositorioUsuarioComun

    @Autowired
    lateinit var instalacionRepository: RepositorioInstalacion

    @Autowired
    lateinit var adminRepository: RepositorioUsuarioAdmin

    @Autowired
    lateinit var entradaRepository: RepositorioEntradas

    @Autowired
    lateinit var logsRepository: RepositorioLogs

    fun getShowByID(id: String): Show {
        return showRepository.findById(id).get()
    }

    fun getInstalacionByID(id: Long): Instalacion {
        return instalacionRepository.findById(id).get()
    }

    fun getListaAmigosVanAShow(idUsuario: Long?, idShow: String): List<UsuarioAmigosDTO> {
        val amigosQueVanAlShow = idUsuario?.let {
            usuarioComunRepository.findAmigosQueVanAlShow(it, idShow)
        }?.map { UsuarioAmigosDTO.fromAmigosDTO(it) } ?: emptyList()
        return amigosQueVanAlShow
    }

    @Transactional(readOnly = true)
    fun getShows(
        idUsuario: Long?,
        artista: String?,
        locacion: String?,
        conAmigos: Boolean? = null
    ): List<ShowDTO> {
        val repoShows = showRepository.findFilteredShows(idUsuario, artista, locacion, LocalDate.now())
        val filteredShows =
            if (conAmigos == true) {
                repoShows.filter { show ->
                    getListaAmigosVanAShow(idUsuario, show.id).isNotEmpty()
                }
            } else repoShows

        return filteredShows.map { show ->
            val amigosQueVanAlShow = getListaAmigosVanAShow(idUsuario, show.id)
            val instalacion = instalacionRepository.findById(show.instalacionId).get()
            println(instalacion)
            ShowDTO.fromShow(show, instalacion, amigosQueVanAlShow)
        }
    }

    @Transactional(readOnly = true)
    fun getShowAdmin(idAdmin: Long?, artista: String?, locacion: String?): List<ShowAdminDTO> {
        val adminUser = adminRepository.findById(idAdmin!!).orElseThrow { UnathorizedUser("Usuario no autorizado") }
        val filteredShows = showRepository.findAdminFilteredShows(adminUser.id, artista, locacion)
        return filteredShows.map { show ->
            val instalacion = instalacionRepository.findById(show.instalacionId).get()
            ShowAdminDTO.fromshowAdminDTO(
                show,
                instalacion,
                calcularRecaudacionShow(show.id),
                getRentabilidadShow(show.id)
            )
        }
    }

    fun calcularRecaudacionShow(showId: String): Double {
        val entradasVendidas = entradaRepository.entradasVendidasByShowId(showId)
        return entradasVendidas.sumOf { it.precioFinal() }
    }

    fun getRentabilidadShow(idShow: String): Double {
        val show = showRepository.findById(idShow).get()
        val instalacion = instalacionRepository.findById(show.instalacionId).get()
        val costoDelShow = show.costoShow(instalacion)
        val recaudacionDelShow = calcularRecaudacionShow(idShow)

        return ((recaudacionDelShow - costoDelShow) / costoDelShow) * 100
    }

    fun entradasVendidasPorUbicacion(ubicacion: Ubicacion, entradas: List<Entrada>): Int {
        return entradas.filter { it.ubicacion == ubicacion && it.estaVendida }.size
    }

    @Transactional(readOnly = true)
    fun getShowDetalles(id: String, idUsuario: Long?): ShowDetalleDTO {
        val show = showRepository.findDetailesById(id)
        val comentarios = usuarioComunRepository.findComentariosDeShow(show.id)
        val usuario = idUsuario?.let {
            usuarioComunRepository.findById(it).orElseThrow { UnathorizedUser("Usuario no identificado y/o existente") }
        }
        val entradas = entradaRepository.entradasVendidasByShowId(show.id)
        val instalacion = instalacionRepository.findById(show.instalacionId).get()
        val comentariosDTO = comentarios.map { ComentarioDTO.fromComentario(it) }
        val recaudacion = calcularRecaudacionShow(show.id)
        val entradasPorUbicacion = instalacion.categorias.associateWith { ubicacion ->
            entradasVendidasPorUbicacion(ubicacion, entradas)
        }
        val personasEnEspera = show.usuariosEnEspera.size
        if (usuario != null) {
            registrarLogClick(show, UsuarioDataLogsDTO.fromUsuarioDataLogs(usuario))
        }
        return ShowDetalleDTO.fromShowDetalle(
            show, instalacion, comentariosDTO, recaudacion, entradas.size, entradasPorUbicacion, personasEnEspera
        )
    }

    @Transactional(readOnly = true)
    fun deleteShow(id: String) {
        val show = showRepository.findById(id)
        showRepository.delete(show.get())
    }

    @Transactional(readOnly = true)
    fun editarDatos(idShow: String, nombreBanda: String, nombreRecital: String) {
        val show = showRepository.findById(idShow).get()
        show.cambiarNombres(nombreBanda, nombreRecital)
        showRepository.save(show)
    }

    @Transactional(readOnly = true)
    fun crearFuncion(idShow: String, dto: FuncionDTO): Funcion {
        val nuevaFuncion = Funcion(
            dto.fecha, dto.hora, dto.precioBaseEntrada, dto.estado, dto.numeroEntradasTotales
        )
        val show = showRepository.findById(idShow).get()
        val instalacion = instalacionRepository.findById(show.instalacionId).get()
        show.agregarFuncion(dto.fecha, dto.hora, instalacion)
        showRepository.save(show)
        return nuevaFuncion
    }

    @Transactional(readOnly = true)
    fun agregarAUsuarioAEspera(idShow: String, idUsuario: Long): UsuarioComun {
        val entradasShows = entradaRepository.todasLasEntradasVendidas(idShow)
        val show = showRepository.findById(idShow).get()
        val usuario = usuarioComunRepository.findById(idUsuario).get()
        if (entradasShows) {
            show.agregarUsuarioEnEspera(usuario.id)
            showRepository.save(show)
        }
        return usuario
    }

    fun registrarLogClick(show: Show, usuario: UsuarioDataLogsDTO) {
        val instalacion = instalacionRepository.findById(show.instalacionId).get()
        val log = LogsDTO().apply {
            id = ObjectId.get().toString()
            fecha = LocalDate.now()
            hora = LocalTime.now()
            nombreAlojamiento = instalacion.nombre
            this.usuario = listOf(usuario)
            idShow = show.id
        }
        logsRepository.save(log)
    }
}
