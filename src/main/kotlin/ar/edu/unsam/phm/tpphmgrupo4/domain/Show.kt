package ar.edu.unsam.phm.tpphmgrupo4.domain
import ar.edu.unsam.phm.tpphmgrupo4.DTO.LogsDTO
import com.mongodb.lang.Nullable
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalTime

@Document(collection = "show")
class Show(
    var nombreBanda: String,
    var nombreRecital: String,
    var imagen: String,
    @Nullable var costoBanda: Double,
    @Nullable var instalacionId: Long,
    @Nullable var nombreInstalacion: String,
    @Nullable var costoInstalacion: Double,
    @Nullable var tipoInstalacion: String,
    var showEstado: Estado = Estado.PrecioBase,
    var funciones: MutableList<Funcion> = mutableListOf(),
){
    @Id
    lateinit var id : String
    var usuariosEnEspera = mutableListOf<Long>()
    var puntaje: Double = 0.00
    var nroComentarios: Int = 0

    fun costoShow(instalacion: Instalacion): Double {
        return costoBanda + instalacion.costoFijo
    }

    fun precioBaseEntrada(instalacion: Instalacion) : Double {
        return costoShow(instalacion) / instalacion.totalCapacidad()
    }

    fun agregarFuncion(fecha: LocalDate, hora: LocalTime, instalacion: Instalacion) {
        funciones.add(Funcion(fecha, hora, precioBaseEntrada(instalacion), showEstado, instalacion.totalCapacidad()))
    }

    fun estaAbierto() : Boolean {
        return funciones.any { it.fecha.isAfter(LocalDate.now()) }
    }

    fun cantidadDeFunciones():Int{
        return funciones.size
    }

    fun showSoldOut():Boolean{
        return funciones.all { it.estaSoldOut }
    }

    fun agregarUsuarioEnEspera(idUser: Long) {
            usuariosEnEspera.add(idUser)
    }

    fun cantidadUsuariosEnEspera(): Int {
        return usuariosEnEspera.size
    }

    fun cambiarNombres(nombreBanda: String, nombreRecital: String) {
        this.nombreBanda = nombreBanda
        this.nombreRecital = nombreRecital
    }


    fun actualizarComentarios(nuevoPuntaje: Double, comentarios: Int) {
        puntaje = nuevoPuntaje
        nroComentarios = comentarios
    }
}