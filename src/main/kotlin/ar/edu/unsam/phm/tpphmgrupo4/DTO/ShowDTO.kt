package ar.edu.unsam.phm.tpphmgrupo4.DTO

import ar.edu.unsam.phm.tpphmgrupo4.domain.Entrada
import ar.edu.unsam.phm.tpphmgrupo4.domain.Instalacion
import ar.edu.unsam.phm.tpphmgrupo4.domain.Show
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.roundToInt

class ShowDTO {
    lateinit var id : String
    lateinit var imagen: String
    lateinit var nombreBanda: String
    lateinit var nombreRecital: String
    lateinit var ubicacion: String
    lateinit var fecha: List<LocalDate>
    lateinit var hora:List<LocalTime>
    var precioLocacionBarata: Double = 0.0
    var precioLocacionCara: Double = 0.0
    lateinit var amigosQueVanAlShow: List<UsuarioAmigosDTO>
    var puntaje: Double? = null
    var comentariosTotales: Int = 0
    companion object {
        fun fromShow(show: Show, instalacion: Instalacion, listaAmigosDTO: List<UsuarioAmigosDTO>): ShowDTO = ShowDTO().also {
            it.id = show.id
            it.imagen = show.imagen
            it.nombreBanda = show.nombreBanda
            it.nombreRecital = show.nombreRecital
            it.ubicacion = instalacion.nombre
            it.fecha = show.funciones.map { it.fecha }
            it.hora = show.funciones.map { it.hora }
            val entradaBarata = Entrada(
                instalacion.ubicacionMenor(), LocalDate.now(),
                show.precioBaseEntrada(instalacion), show.showEstado, show.funciones[0].id, show.id
            )
            val entradaCara = Entrada(
                instalacion.ubicacionMayor(), LocalDate.now(),
                show.precioBaseEntrada(instalacion), show.showEstado, show.funciones[0].id, show.id
            )
            it.precioLocacionBarata = entradaBarata.precioFinal().roundToInt().toDouble()
            it.precioLocacionCara = entradaCara.precioFinal().roundToInt().toDouble()
            it.amigosQueVanAlShow = listaAmigosDTO
            it.puntaje = show.puntaje
            it.comentariosTotales = show.nroComentarios
        }
    }
}