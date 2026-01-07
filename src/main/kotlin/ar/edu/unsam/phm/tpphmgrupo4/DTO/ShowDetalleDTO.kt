package ar.edu.unsam.phm.tpphmgrupo4.DTO

import ar.edu.unsam.phm.tpphmgrupo4.domain.Instalacion
import ar.edu.unsam.phm.tpphmgrupo4.domain.Show
import ar.edu.unsam.phm.tpphmgrupo4.domain.Ubicacion
import java.time.LocalDate
import java.time.LocalTime

class ShowDetalleDTO {
    lateinit var id : String
    lateinit var fotoArtista: String
    lateinit var nombreBanda: String
    lateinit var nombreRecital: String
    lateinit var ubicacion: String
    lateinit var fecha: List<LocalDate>
    lateinit var hora:List<LocalTime>
    var puntaje: Double? = null
    var cantidadComentario : Int ? = 0
    lateinit var comentarios: List<ComentarioDTO>
    lateinit var souldOut: Number
    var allSouldOut: Boolean = false
    var funcionesDisponibles : Boolean = false
    var usuarioEnEspera: Int ?= 0
    var totalRecaudado: Double? = null
    var totalCosto: Double? = null
    lateinit var entradasVendidasTotales: Number
    lateinit var logitud : String
    lateinit var latitud : String
    lateinit var ubicacionCosto: Map<Ubicacion, Int>
    lateinit var entradasVendidasPorUbicacion: Map<Ubicacion, Int>


    companion object {
        fun fromShowDetalle(
            show: Show,
            instalacion: Instalacion,
            comentarios: List<ComentarioDTO>,
            recaudacion: Double,
            entradasVendidas: Int,
            entradasPorUbicacion: Map<Ubicacion, Int>,
            usuarioEnEspera : Int
        ): ShowDetalleDTO = ShowDetalleDTO().also {
            it.id = show.id
            it.fotoArtista = show.imagen
            it.nombreBanda = show.nombreBanda
            it.nombreRecital = show.nombreRecital
            it.ubicacion = instalacion.nombre
            it.fecha = show.funciones.map { f -> f.fecha }
            it.hora = show.funciones.map { f -> f.hora }
            it.puntaje = show.puntaje
            it.cantidadComentario = show.nroComentarios
            it.comentarios = comentarios
            it.souldOut = show.funciones.filter { f -> f.estaSoldOut }.size
            it.allSouldOut = show.funciones.size == it.souldOut
            it.funcionesDisponibles = show.funciones.size == it.souldOut
            it.usuarioEnEspera = usuarioEnEspera
            it.totalRecaudado = recaudacion
            it.totalCosto = show.costoShow(instalacion)
            it.entradasVendidasTotales = entradasVendidas
            it.latitud = instalacion.latitud
            it.logitud = instalacion.longitud
            it.ubicacionCosto = instalacion.obtenerCostosUbicaciones()
            it.entradasVendidasPorUbicacion = entradasPorUbicacion

        }
    }
}