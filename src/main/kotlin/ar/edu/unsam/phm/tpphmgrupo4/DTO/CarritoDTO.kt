package ar.edu.unsam.phm.tpphmgrupo4.DTO

import ar.edu.unsam.phm.tpphmgrupo4.domain.Entrada
import ar.edu.unsam.phm.tpphmgrupo4.domain.Instalacion
import ar.edu.unsam.phm.tpphmgrupo4.domain.Show
import ar.edu.unsam.phm.tpphmgrupo4.domain.Carrito
import java.time.LocalDate
import kotlin.math.roundToInt

class CarritoDTO {
    var id :Int?=null
    lateinit var idShow : String
    var precioEntrada: Double ?= null
    var precioTotalCarrito: Double = 0.0
    lateinit var imagen: String
    lateinit var nombreBanda: String
    lateinit var nombreRecital: String
    lateinit var ubicacion: String
    lateinit var fecha: List<LocalDate>
    var puntaje: Double? = null
    var comentariosTotales: Int = 0
    var sizeCarrito : Int ?= 0
    lateinit var amigosQueVanAlShow: List<UsuarioAmigosDTO>
    var estaAbierto : Boolean ?= true

    companion object {
        fun toEntradaCarritoDTO(entrada: Entrada, show: Show, instalacion: Instalacion, carrito: Carrito, listaAmigosDTO: List<UsuarioAmigosDTO>): CarritoDTO = CarritoDTO().also {
            it.id = entrada.id!!.toInt()
            it.idShow = show.id
            it.imagen = show.imagen
            it.nombreBanda = show.nombreBanda
            it.nombreRecital = show.nombreRecital
            it.ubicacion = instalacion.nombre
            it.fecha = show.funciones.map { it.fecha }
            it.puntaje = show.puntaje
            it.comentariosTotales = show.nroComentarios
            it.precioEntrada = entrada.precioFinal().roundToInt().toDouble()
            it.precioTotalCarrito = carrito.totalCarrito()
            it.sizeCarrito = carrito.entradasEnCarrito()
            it.amigosQueVanAlShow = listaAmigosDTO
            it.estaAbierto = show.estaAbierto()
        }
    }


}