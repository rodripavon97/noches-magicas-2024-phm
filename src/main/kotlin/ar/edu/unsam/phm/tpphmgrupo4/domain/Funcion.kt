package ar.edu.unsam.phm.tpphmgrupo4.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalTime

@Document(collection = "funcion")
class Funcion(
    var fecha: LocalDate = LocalDate.now(),
    var hora: LocalTime = LocalTime.now(),
    var precioBaseEntrada: Double = 0.0,
    var showEstado: Estado,
    var numeroEntradasTotales: Int = 0
) {
    @Id
    var id: String = ObjectId.get().toString()
    var estaSoldOut: Boolean = false

    fun funcionAgotada() {
        estaSoldOut = true
    }
}