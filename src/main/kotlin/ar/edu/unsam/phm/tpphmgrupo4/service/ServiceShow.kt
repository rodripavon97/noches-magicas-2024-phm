package ar.edu.unsam.phm.tpphmgrupo4.service

import ar.edu.unsam.phm.tpphmgrupo4.DTO.*
import ar.edu.unsam.phm.tpphmgrupo4.Exceptions.UnathorizedUser
import ar.edu.unsam.phm.tpphmgrupo4.domain.*
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioEntradas
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioInstalacion
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioUsuarioAdmin
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.JPARepository.RepositorioUsuarioComun
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.MongoRepository.RepositorioLogs
import ar.edu.unsam.phm.tpphmgrupo4.repositorio.MongoRepository.RepositorioShow
import java.time.LocalDate
import java.time.LocalTime

/**
 * Servicio responsable de las operaciones principales de Shows.
 * 
 * Principios aplicados:
 * - SRP: Una sola responsabilidad - operaciones de shows
 * - DIP: Depende de abstracciones (repositorios y servicios inyectados)
 * 
 * Responsabilidades:
 * - Consulta y filtrado de shows
 * - Gestión de funciones
 * - Edición de datos de shows
 * - Lista de espera
 * - Registro de logs de visualización
 * 
 * Otras responsabilidades fueron delegadas a:
 * - RecaudacionService: cálculos financieros
 * - ComentarioService: gestión de comentarios
 */
@Service
class ServiceShow(
    private val showRepository: RepositorioShow,
    private val usuarioComunRepository: RepositorioUsuarioComun,
    private val instalacionRepository: RepositorioInstalacion,
    private val adminRepository: RepositorioUsuarioAdmin,
    private val entradaRepository: RepositorioEntradas,
    private val logsRepository: RepositorioLogs,
    private val recaudacionService: RecaudacionService
) {

    fun findShowById(showId: String): Show {
        return showRepository.findById(showId)
            .orElseThrow { NoSuchElementException("Show no encontrado con ID: $showId") }
    }

    fun findInstalacionById(instalacionId: Long): Instalacion {
        return instalacionRepository.findById(instalacionId)
            .orElseThrow { NoSuchElementException("Instalación no encontrada con ID: $instalacionId") }
    }

    fun obtenerAmigosQueAsistenAlShow(usuarioId: Long?, showId: String): List<UsuarioAmigosDTO> {
        return usuarioId?.let {
            usuarioComunRepository.findAmigosQueVanAlShow(it, showId)
                .map { amigo -> UsuarioAmigosDTO.fromAmigosDTO(amigo) }
        } ?: emptyList()
    }
    @Transactional(readOnly = true)
    fun buscarShows(
        usuarioId: Long?,
        nombreArtista: String?,
        nombreLocacion: String?,
        soloConAmigos: Boolean? = false
    ): List<ShowDTO> {
        val shows = if (soloConAmigos == true && usuarioId != null) {
            val showIdsConAmigos = usuarioComunRepository.findShowIdsConAmigos(usuarioId)
            if (showIdsConAmigos.isEmpty()) {
                emptyList()
            } else {
                showRepository.findShowsByIdsAndFilters(showIdsConAmigos, nombreArtista, nombreLocacion, LocalDate.now())
            }
        } else {
            showRepository.findFilteredShows(usuarioId, nombreArtista, nombreLocacion, LocalDate.now())
        }

        return shows.map { show ->
            val amigosQueAsisten = obtenerAmigosQueAsistenAlShow(usuarioId, show.id)
            val instalacion = instalacionRepository.findById(show.instalacionId)
                .orElseThrow { NoSuchElementException("Instalación no encontrada con ID: ${show.instalacionId}") }
            ShowDTO.fromShow(show, instalacion, amigosQueAsisten)
        }
    }

    @Transactional(readOnly = true)
    fun buscarShowsParaAdmin(adminId: Long?, nombreArtista: String?, nombreLocacion: String?): List<ShowAdminDTO> {
        requireNotNull(adminId) { "El ID del administrador no puede ser nulo" }
        
        val admin = adminRepository.findById(adminId).orElseThrow { 
            UnathorizedUser("Usuario no autorizado") 
        }
        val shows = showRepository.findAdminFilteredShows(admin.id, nombreArtista, nombreLocacion)
        
        return shows.map { show ->
            val instalacion = instalacionRepository.findById(show.instalacionId)
                .orElseThrow { NoSuchElementException("Instalación no encontrada con ID: ${show.instalacionId}") }
            ShowAdminDTO.fromshowAdminDTO(
                show,
                instalacion,
                recaudacionService.calcularRecaudacionShow(show.id),
                recaudacionService.calcularRentabilidadShow(show.id)
            )
        }
    }
    @Transactional(readOnly = true)
    fun obtenerDetallesShow(showId: String, usuarioId: Long?): ShowDetalleDTO {
        val show = showRepository.findDetailesById(showId)
        val comentarios = usuarioComunRepository.findComentariosDeShow(show.id)
        val usuario = usuarioId?.let {
            usuarioComunRepository.findById(it).orElseThrow { 
                UnathorizedUser("Usuario no identificado y/o existente") 
            }
        }
        val entradasVendidas = entradaRepository.entradasVendidasByShowId(show.id)
        val instalacion = instalacionRepository.findById(show.instalacionId)
            .orElseThrow { NoSuchElementException("Instalación no encontrada con ID: ${show.instalacionId}") }
        val comentariosDTO = comentarios.map { ComentarioDTO.fromComentario(it) }
        val recaudacion = recaudacionService.calcularRecaudacionShow(show.id)
        val estadisticasPorUbicacion = recaudacionService.obtenerEstadisticasPorUbicacion(show.id)
        val cantidadPersonasEnEspera = show.usuariosEnEspera.size
        
        usuario?.let {
            registrarLogVisualizacion(show, UsuarioDataLogsDTO.fromUsuarioDataLogs(it))
        }
        
        return ShowDetalleDTO.fromShowDetalle(
            show, instalacion, comentariosDTO, recaudacion, entradasVendidas.size, estadisticasPorUbicacion, cantidadPersonasEnEspera
        )
    }
    @Transactional(readOnly = true)
    fun eliminarShow(showId: String) {
        val show = showRepository.findById(showId)
            .orElseThrow { NoSuchElementException("Show no encontrado con ID: $showId") }
        showRepository.delete(show)
    }

    @Transactional(readOnly = true)
    fun actualizarDatosShow(showId: String, nombreBanda: String, nombreRecital: String) {
        require(nombreBanda.isNotBlank()) { "El nombre de la banda no puede estar vacío" }
        require(nombreRecital.isNotBlank()) { "El nombre del recital no puede estar vacío" }
        
        val show = showRepository.findById(showId)
            .orElseThrow { NoSuchElementException("Show no encontrado con ID: $showId") }
        show.cambiarNombres(nombreBanda, nombreRecital)
        showRepository.save(show)
    }
    @Transactional(readOnly = true)
    fun crearFuncion(showId: String, dto: FuncionDTO): Funcion {
        val nuevaFuncion = Funcion(
            dto.fecha, dto.hora, dto.precioBaseEntrada, dto.estado, dto.numeroEntradasTotales
        )
        val show = showRepository.findById(showId)
            .orElseThrow { NoSuchElementException("Show no encontrado con ID: $showId") }
        val instalacion = instalacionRepository.findById(show.instalacionId)
            .orElseThrow { NoSuchElementException("Instalación no encontrada con ID: ${show.instalacionId}") }
        show.agregarFuncion(dto.fecha, dto.hora, instalacion)
        showRepository.save(show)
        return nuevaFuncion
    }
    @Transactional(readOnly = true)
    fun agregarUsuarioAListaEspera(showId: String, usuarioId: Long): UsuarioComun {
        val todasEntradasVendidas = entradaRepository.todasLasEntradasVendidas(showId)
        val show = showRepository.findById(showId)
            .orElseThrow { NoSuchElementException("Show no encontrado con ID: $showId") }
        val usuario = usuarioComunRepository.findById(usuarioId)
            .orElseThrow { NoSuchElementException("Usuario no encontrado con ID: $usuarioId") }
        
        if (todasEntradasVendidas) {
            show.agregarUsuarioEnEspera(usuario.id)
            showRepository.save(show)
        }
        return usuario
    }

    fun registrarLogVisualizacion(show: Show, usuario: UsuarioDataLogsDTO) {
        val instalacion = instalacionRepository.findById(show.instalacionId)
            .orElseThrow { NoSuchElementException("Instalación no encontrada con ID: ${show.instalacionId}") }
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
