package ar.edu.unsam.phm.tpphmgrupo4.Controller

import ar.edu.unsam.phm.tpphmgrupo4.DTO.FuncionDTO
import ar.edu.unsam.phm.tpphmgrupo4.DTO.ShowAdminDTO
import ar.edu.unsam.phm.tpphmgrupo4.DTO.ShowDTO
import ar.edu.unsam.phm.tpphmgrupo4.DTO.ShowDetalleDTO
import ar.edu.unsam.phm.tpphmgrupo4.domain.Funcion
import ar.edu.unsam.phm.tpphmgrupo4.domain.UsuarioComun
import ar.edu.unsam.phm.tpphmgrupo4.service.ServiceShow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 * Controller refactorizado siguiendo principios SOLID.
 * Mantiene su enfoque en operaciones de Show, delegando cálculos
 * complejos al RecaudacionService (a través de ServiceShow).
 * 
 * Mejoras aplicadas:
 * - ServiceShow ahora delega responsabilidades financieras a RecaudacionService
 * - Mejor separación de concerns
 * - Código más mantenible y testeable
 */
@RestController
@CrossOrigin(origins = ["*"], methods= [RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.PATCH, RequestMethod.DELETE, RequestMethod.POST])
class ShowController {
    @Autowired
    lateinit var showService: ServiceShow

    @GetMapping("/shows")
    fun getShowsAll(
        @RequestParam(required = false) id: Long?,
        @RequestParam(required = false, defaultValue = "") artista: String?,
        @RequestParam(required = false, defaultValue = "") locacion: String?,
        @RequestParam(required = false) conAmigos: Boolean?
    ): List<ShowDTO> {
        return showService.getShows(id, artista, locacion, conAmigos)
    }

    @GetMapping("/show-detalle/{id}")
    fun ShowPorID(@PathVariable id: String): ShowDetalleDTO {
        return showService.getShowDetalles(id, null)
    }

    @GetMapping("/admin/shows")
    fun getAdminShows(
        @RequestParam(required = false, defaultValue = "") artista: String?, 
        @RequestParam(required = false, defaultValue = "") locacion: String?,  
        @RequestParam(required = false, defaultValue = "") id: Long?
    ): List<ShowAdminDTO> {
        return showService.getShowAdmin(id, artista, locacion)
    }

    @PostMapping("/show/{idShow}/fila-espera/{idUsuario}")
    fun agregrarPersonaEnEspera(@PathVariable idShow: String, @PathVariable idUsuario: Long): UsuarioComun {
        return showService.agregarAUsuarioAEspera(idShow, idUsuario)
    }

    @PostMapping("/show/{id}/nueva-funcion")
    fun agregrarFuncion(@PathVariable id: String, @RequestBody dto: FuncionDTO): Funcion {
        return showService.crearFuncion(id, dto)
    }

    @DeleteMapping("/show/{id}")
    fun eliminarShow(@PathVariable id: String) {
        showService.deleteShow(id)
    }

    @PatchMapping("/show/{id}")
    fun editarDatos(@PathVariable id: String, @RequestBody showDTO: ShowDTO) {
        showService.editarDatos(id, showDTO.nombreBanda, showDTO.nombreRecital)
    }

    @PostMapping("/show/{idShow}/log/{idUsuario}")
    fun registrarLogClick(
        @PathVariable idShow: String,
        @PathVariable idUsuario: Long
    ): ShowDetalleDTO {
        return showService.getShowDetalles(idShow, idUsuario)
    }
}
