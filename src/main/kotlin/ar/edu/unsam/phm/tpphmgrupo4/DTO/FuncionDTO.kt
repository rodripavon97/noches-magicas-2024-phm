package ar.edu.unsam.phm.tpphmgrupo4.DTO


import ar.edu.unsam.phm.tpphmgrupo4.domain.Estado
import ar.edu.unsam.phm.tpphmgrupo4.domain.Funcion
import ar.edu.unsam.phm.tpphmgrupo4.domain.Show
import java.time.LocalDate
import java.time.LocalTime

class FuncionDTO {
    lateinit var idShow: String
    var id: Int = 0
    lateinit var fecha: LocalDate
    lateinit var hora: LocalTime
    var precioBaseEntrada: Double = 0.0
    lateinit var estado: Estado
    var numeroEntradasTotales: Int = 0


    companion object {
        fun fromShow(funcion: Funcion, show: Show): FuncionDTO = FuncionDTO().also {
            it.id = funcion.id.toInt()
            it.fecha = funcion.fecha
            it.hora = funcion.hora
            it.precioBaseEntrada = funcion.precioBaseEntrada
            it.estado = when (funcion.showEstado) {
                Estado.PrecioBase -> Estado.PrecioBase
                Estado.VentaPlena -> Estado.VentaPlena
                Estado.Megashow -> Estado.Megashow
            }
            it.numeroEntradasTotales = funcion.numeroEntradasTotales
            it.idShow = show.id


        }
    }
}