package ar.edu.unsam.phm.tpphmgrupo4.domain

import ar.edu.unsam.phm.tpphmgrupo4.Exceptions.*
import ar.edu.unsam.phm.tpphmgrupo4.domain.node.UsuarioNodo
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.persistence.*
import jakarta.persistence.Entity
import jakarta.persistence.Transient
import org.springframework.data.neo4j.core.schema.Id as Neo4jId
import java.time.LocalDate
import java.time.LocalTime
import java.time.Period

@Entity
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = UsuarioComun::class, name = "USUARIO"),
    JsonSubTypes.Type(value = UsuarioAdmin::class, name = "ADMIN")
)
@Inheritance(strategy = InheritanceType.JOINED)
abstract class Usuario {
    @Id
    @Neo4jId
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
    abstract var nombre: String
    abstract var apellido: String
    abstract var fechaDeNacimiento: LocalDate
    abstract var fotoPerfil: String
    abstract val esAdmin: Boolean
    abstract var username: String
    abstract var password: String


    fun calcularIngresosFuturos(show: Show): Double {
        return show.funciones.size * show.costoBanda
    }
}
@Entity
class UsuarioComun(
    @Column(nullable = false) override var nombre: String = "",
    @Column(nullable = false) override var apellido: String = "",
    @Column override var fechaDeNacimiento: LocalDate = LocalDate.now(),
    @Column(nullable = false) override var username: String = "",
    @Column(nullable = false) override var password: String = "",
    @Column override var fotoPerfil: String = "",
) : Usuario() {

    @Column
    override var esAdmin = false
    var edad = Period.between(fechaDeNacimiento, LocalDate.now()).years
    @Column(nullable = false, unique = true)
    var dni: Int = 0
    @Column(columnDefinition = "DOUBLE PRECISION CHECK (saldo >= 0)")
    var saldo: Double = 0.0
    @ElementCollection(fetch = FetchType.LAZY)
    var amigos: MutableList<Int> = mutableListOf()

    @Transient
    var amigues: MutableList<UsuarioNodo> = mutableListOf()


    @OneToMany(fetch = FetchType.LAZY)
    var entradasCompradas: MutableList<Entrada> = mutableListOf()
    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    var comentarios: MutableList<Comentario> = mutableListOf()
    fun cambiarNombres(nombre: String, apellido: String) {
        if (nombre.isNotEmpty() && apellido.isNotEmpty()) {
            this.nombre = nombre
            this.apellido = apellido
        } else {
            throw UnauthorizedEditData("No puede dejar vacio el campo nombre y/o apellido ")
        }

    }

    fun aumentarSaldo(monto: Double) {
        if (monto > 0.0) {
            saldo += monto
        } else {
            throw SaldoValidationException("El saldo no puede ser negativo o 0")
        }
    }

    fun disminuirSaldo(monto: Double) {
        saldo -= monto
    }

    fun agregarAmigo(amigoId: Int) {
        amigos.add(amigoId)
    }

    fun quitarAmigo(amigoId: Int) {
        amigos.remove(amigoId)
    }

    fun listaIdShows(): List<String> {
        return entradasCompradas.map { it.showId }
    }

    fun dejarComentario(show: Show, contenido: String, puntuacion: Float) {
        val funcionesEntradasId = entradasCompradas.map { it.funcionId }
        val funcionesShow = show.funciones
        val funcionEnComun = funcionesShow.filter { funcionesEntradasId.contains(it.id) }
        if (funcionEnComun.isNotEmpty()) {
            if (funcionEnComun[0].fecha.isBefore(LocalDate.now())) {
                if (this.comentarios.all { it.idShow != show.id }) {
                    val comentarioCompleto = Comentario(
                        show.id,
                        this.fotoPerfil,
                        show.imagen,
                        this.username,
                        show.nombreBanda,
                        LocalDate.now(),
                        contenido,
                        puntuacion
                    )
                    comentarios.add(comentarioCompleto)
                } else {
                    throw ComentarioExistenteException("Ya se ha dejado un comentario en este show")
                }
            } else {
                throw ComentarioTempranoException("Este show todavía no ocurrió")
            }
        } else {
            throw ComentarioSinEntradaException("El usuario no tiene entradas para este show")
        }
    }

    fun borrarComentario(idShow: String) {
        comentarios.removeIf{it.idShow == idShow }
    }
}


@Entity
class UsuarioAdmin(
    @Column(nullable = false)
    override var nombre: String = "",
    @Column(nullable = false)
    override var apellido: String = "",
    @Column
    override var fechaDeNacimiento: LocalDate = LocalDate.now(),
    @Column(nullable = false, unique = true)
    override var username: String = "",
    @Column(nullable = false)
    override var password: String = "",
    @Column
    override var fotoPerfil: String,
) : Usuario() {
    @Column
    override var esAdmin = true
    fun agregarFuncion(show: Show, instalacion: Instalacion, fecha: LocalDate, hora: LocalTime) {
        if (show.showSoldOut()) {
            val precioBase = show.precioBaseEntrada(instalacion)
            val ingresosPotenciales = precioBase * instalacion.totalCapacidad()

            val costoShow = show.costoShow(instalacion)

            if (ingresosPotenciales > costoShow) {
                show.agregarFuncion(fecha, hora, instalacion)
            } else {
                throw RuntimeException("Crear una nueva función no es redituable en este momento")
            }
        } else {
            throw RuntimeException("Todavía quedan entradas para este show")
        }
    }
}