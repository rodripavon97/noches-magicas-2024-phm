package ar.edu.unsam.phm.tpphmgrupo4.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.persistence.*
import org.springframework.data.mongodb.core.mapping.Field


@Entity
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = Teatro::class, name = "TEATRO"),
    JsonSubTypes.Type(value = Estadio::class, name = "ESTADIO")
)
@Inheritance(strategy = InheritanceType.JOINED)
abstract class Instalacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    var nombre: String = ""

    @Column(nullable = false)
    var tipoInstalacion: String = ""

    @Column(nullable = false)
    open var longitud: String = ""

    @Column(nullable = false)
    open var latitud: String = ""

    @Column()
    open var costoFijo: Double = 0.00


    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @Field("categorias_instalacion")
    open var categorias: MutableList<Ubicacion> = mutableListOf()

    abstract fun totalCapacidad(): Int
    fun costoUbicacion(tipoUbicacion: Ubicacion): Int {
        return tipoUbicacion.costo
    }
    fun obtenerCostosUbicaciones(): Map<Ubicacion, Int> {
        return categorias.associateWith { ubicacion -> ubicacion.costo }
    }
    fun ubicacionMayor(): Ubicacion {
        return categorias.first()
    }
    fun ubicacionMenor(): Ubicacion {
        return categorias.last()
    }
    fun obtenerUbicaciones(): List<Ubicacion>{
        return categorias
    }


}

@Entity
class Teatro(
    @Column
    val buenaAcustica: Boolean = false,
    @Column
    var capacidadPlateaBaja: Int = 0,
    @Column
    var capacidadPullman: Int = 0
) : Instalacion() {
    init {
        tipoInstalacion = "TEATRO"
        costoFijo = if (buenaAcustica) 150000.00 else 100000.00
    }


    @ElementCollection
    @Field("categorias_teatro")
    override var categorias = mutableListOf(
        Ubicacion.PlateaBaja,
        Ubicacion.Pullman
    )

    override fun totalCapacidad(): Int {
        return capacidadPlateaBaja + capacidadPullman
    }

}

@Entity
class Estadio(
    var costoFijoEstadio: Double = 0.0,
    @Column
    var capacidadPalco: Int =0,
    @Column
    var capacidadCampo: Int = 0,
    @Column
    var capacidadPlateaAlta: Int = 0
) : Instalacion() {
    init {
        tipoInstalacion = "ESTADIO"
        costoFijo = costoFijoEstadio
    }

    @ElementCollection
    @Field("categorias_estadio")
    override var categorias = mutableListOf(
        Ubicacion.Palco,
        Ubicacion.Campo,
        Ubicacion.PlateaAlta
    )

    override fun totalCapacidad(): Int {
        return capacidadPalco + capacidadPlateaAlta + capacidadCampo
    }
}