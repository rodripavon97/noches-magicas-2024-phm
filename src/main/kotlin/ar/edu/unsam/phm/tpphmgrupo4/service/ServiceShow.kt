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
 * Refactorizado siguiendo el principio SRP (Single Responsibility Principle).
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
 * - ComentarioService: gestión de comentarios (ya no está aquí el traer comentarios)
 */
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
    
    @Autowired
    lateinit var recaudacionService: RecaudacionService

    /**
     * Obtiene un show por su ID.
     * @param id ID del show
     * @return Show encontrado
     */
    fun getShowByID(id: String): Show {
        return showRepository.findById(id).get()
    }

    /**
     * Obtiene una instalación por su ID.
     * @param id ID de la instalación
     * @return Instalación encontrada
     */
    fun getInstalacionByID(id: Long): Instalacion {
        return instalacionRepository.findById(id).get()
    }

    /**
     * Obtiene la lista de amigos que van a un show específico.
     * @param idUsuario ID del usuario (opcional)
     * @param idShow ID del show
     * @return Lista de amigos que asisten al show
     */
    fun getListaAmigosVanAShow(idUsuario: Long?, idShow: String): List<UsuarioAmigosDTO> {
        val amigosQueVanAlShow = idUsuario?.let {
            usuarioComunRepository.findAmigosQueVanAlShow(it, idShow)
        }?.map { UsuarioAmigosDTO.fromAmigosDTO(it) } ?: emptyList()
        return amigosQueVanAlShow
    }

    /**
     * Obtiene shows filtrados según criterios.
     * Optimizado: Cuando se filtra por amigos, obtiene los IDs primero y filtra en DB.
     * @param idUsuario ID del usuario (opcional)
     * @param artista Filtro por artista
     * @param locacion Filtro por locación
     * @param conAmigos Filtro para mostrar solo shows con amigos
     * @return Lista de shows filtrados
     */
    @Transactional(readOnly = true)
    fun getShows(
        idUsuario: Long?,
        artista: String?,
        locacion: String?,
        conAmigos: Boolean? = null
    ): List<ShowDTO> {
        // Optimizado: Si se filtra por amigos, obtener IDs primero y filtrar en MongoDB
        val repoShows = if (conAmigos == true && idUsuario != null) {
            val showIdsConAmigos = usuarioComunRepository.findShowIdsConAmigos(idUsuario)
            if (showIdsConAmigos.isEmpty()) {
                emptyList()
            } else {
                showRepository.findShowsByIdsAndFilters(showIdsConAmigos, artista, locacion, LocalDate.now())
            }
        } else {
            showRepository.findFilteredShows(idUsuario, artista, locacion, LocalDate.now())
        }

        return repoShows.map { show ->
            val amigosQueVanAlShow = getListaAmigosVanAShow(idUsuario, show.id)
            val instalacion = instalacionRepository.findById(show.instalacionId).get()
            ShowDTO.fromShow(show, instalacion, amigosQueVanAlShow)
        }
    }

    /**
     * Obtiene shows para la vista de administrador.
     * Incluye información de recaudación y rentabilidad.
     * @param idAdmin ID del administrador
     * @param artista Filtro por artista
     * @param locacion Filtro por locación
     * @return Lista de shows con información administrativa
     */
    @Transactional(readOnly = true)
    fun getShowAdmin(idAdmin: Long?, artista: String?, locacion: String?): List<ShowAdminDTO> {
        val adminUser = adminRepository.findById(idAdmin!!).orElseThrow { 
            UnathorizedUser("Usuario no autorizado") 
        }
        val filteredShows = showRepository.findAdminFilteredShows(adminUser.id, artista, locacion)
        
        return filteredShows.map { show ->
            val instalacion = instalacionRepository.findById(show.instalacionId).get()
            ShowAdminDTO.fromshowAdminDTO(
                show,
                instalacion,
                recaudacionService.calcularRecaudacionShow(show.id),
                recaudacionService.calcularRentabilidadShow(show.id)
            )
        }
    }

    /**
     * Obtiene los detalles completos de un show.
     * Incluye comentarios, entradas vendidas y estadísticas.
     * @param id ID del show
     * @param idUsuario ID del usuario que consulta (opcional, para registrar log)
     * @return Detalles completos del show
     */
    @Transactional(readOnly = true)
    fun getShowDetalles(id: String, idUsuario: Long?): ShowDetalleDTO {
        val show = showRepository.findDetailesById(id)
        val comentarios = usuarioComunRepository.findComentariosDeShow(show.id)
        val usuario = idUsuario?.let {
            usuarioComunRepository.findById(it).orElseThrow { 
                UnathorizedUser("Usuario no identificado y/o existente") 
            }
        }
        val entradas = entradaRepository.entradasVendidasByShowId(show.id)
        val instalacion = instalacionRepository.findById(show.instalacionId).get()
        val comentariosDTO = comentarios.map { ComentarioDTO.fromComentario(it) }
        val recaudacion = recaudacionService.calcularRecaudacionShow(show.id)
        val entradasPorUbicacion = recaudacionService.obtenerEstadisticasPorUbicacion(show.id)
        val personasEnEspera = show.usuariosEnEspera.size
        
        if (usuario != null) {
            registrarLogClick(show, UsuarioDataLogsDTO.fromUsuarioDataLogs(usuario))
        }
        
        return ShowDetalleDTO.fromShowDetalle(
            show, instalacion, comentariosDTO, recaudacion, entradas.size, entradasPorUbicacion, personasEnEspera
        )
    }

    /**
     * Elimina un show del sistema.
     * @param id ID del show a eliminar
     */
    @Transactional(readOnly = true)
    fun deleteShow(id: String) {
        val show = showRepository.findById(id)
        showRepository.delete(show.get())
    }

    /**
     * Edita los datos básicos de un show.
     * @param idShow ID del show
     * @param nombreBanda Nuevo nombre de banda
     * @param nombreRecital Nuevo nombre del recital
     */
    @Transactional(readOnly = true)
    fun editarDatos(idShow: String, nombreBanda: String, nombreRecital: String) {
        val show = showRepository.findById(idShow).get()
        show.cambiarNombres(nombreBanda, nombreRecital)
        showRepository.save(show)
    }

    /**
     * Crea una nueva función para un show.
     * @param idShow ID del show
     * @param dto Datos de la nueva función
     * @return Función creada
     */
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

    /**
     * Agrega un usuario a la lista de espera de un show.
     * Solo se agrega si todas las entradas están vendidas.
     * @param idShow ID del show
     * @param idUsuario ID del usuario
     * @return Usuario agregado a la espera
     */
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

    /**
     * Registra un log de visualización de show por un usuario.
     * @param show Show visualizado
     * @param usuario Usuario que visualiza
     */
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
