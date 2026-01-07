package ar.edu.unsam.phm.tpphmgrupo4.domain

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.springframework.data.neo4j.core.schema.Node
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import java.time.LocalDate

@Entity
class Entrada(
    @Column()
    var ubicacion: Ubicacion,
    @Column(nullable = false)
    var fechaEntrada: LocalDate = LocalDate.now(),
    @Column()
    var precioBase: Double = 0.0,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var showEstado: Estado,
    @Column(nullable = false)
    var funcionId: String = "",
    @Column(nullable = false)
    var showId : String = ""
){
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long ?= 0

    @Column
    var estaVendida = false


    fun cambiarEstado(estado: Estado) {
        showEstado = estado
    }

    fun precio(): Double {
        return ubicacion.costo + precioBase
    }

    @JsonProperty("precioFinal")
    fun precioFinal(): Double {
        return precio() * showEstado.rentabilidad
    }

    fun vender() {
        estaVendida = true
    }

}