package ar.edu.unsam.phm.tpphmgrupo4.domain

import jakarta.persistence.*
import java.time.LocalDate

@Entity
class Comentario(
    @Column
    var idShow: String = "",
    @Column
    var fotoUsuario: String = "",
    @Column
    var fotoBanda: String ="",
    @Column
    var nombreUsuario: String = "",
    @Column
    var nombreBanda: String = "",
    @Column
    var fecha: LocalDate = LocalDate.now(),
    @Column
    var contenido: String = "",
    @Column
    var puntuacion: Float = 0.0F,
){
 @Id @GeneratedValue(strategy = GenerationType.AUTO)
 var id: Long = 0
}