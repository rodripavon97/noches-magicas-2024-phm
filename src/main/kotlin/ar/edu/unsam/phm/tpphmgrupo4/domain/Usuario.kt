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

/**
 * Clase base abstracta para todos los tipos de usuarios del sistema.
 * 
 * Principios aplicados:
 * - LSP: Los subtipos (UsuarioComun, UsuarioAdmin) son sustituibles por Usuario
 * - OCP: Abierto para extensión (nuevos tipos de usuario), cerrado para modificación
 */
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
        require(nombre.isNotBlank()) { "El nombre no puede estar vacío" }
        require(apellido.isNotBlank()) { "El apellido no puede estar vacío" }
        
        this.nombre = nombre
        this.apellido = apellido
    }

    fun aumentarSaldo(monto: Double) {
        require(monto > 0.0) { "El monto debe ser mayor a cero" }
        saldo += monto
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
        require(contenido.isNotBlank()) { "El contenido del comentario no puede estar vacío" }
        require(puntuacion in 0.0..5.0) { "La puntuación debe estar entre 0 y 5" }
        
        val funcionesCompradas = entradasCompradas.map { it.funcionId }
        val funcionesDelShow = show.funciones.filter { funcionesCompradas.contains(it.id) }
        
        if (funcionesDelShow.isEmpty()) {
            throw ComentarioSinEntradaException("El usuario no tiene entradas para este show")
        }
        
        val primeraFuncion = funcionesDelShow.first()
        if (primeraFuncion.fecha.isAfter(LocalDate.now()) || primeraFuncion.fecha.isEqual(LocalDate.now())) {
            throw ComentarioTempranoException("Este show todavía no ocurrió")
        }
        
        if (comentarios.any { it.idShow == show.id }) {
            throw ComentarioExistenteException("Ya se ha dejado un comentario en este show")
        }
        
        val nuevoComentario = Comentario(
            show.id,
            this.fotoPerfil,
            show.imagen,
            this.username,
            show.nombreBanda,
            LocalDate.now(),
            contenido,
            puntuacion
        )
        comentarios.add(nuevoComentario)
    }

    fun borrarComentario(showId: String) {
        comentarios.removeIf { it.idShow == showId }
    }
}


/**
 * Usuario con permisos de administrador.
 * Puede gestionar shows y funciones.
 */
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
        require(show.showSoldOut()) { "Todavía quedan entradas para este show" }
        
        val precioBase = show.precioBaseEntrada(instalacion)
        val ingresosPotenciales = precioBase * instalacion.totalCapacidad()
        val costoShow = show.costoShow(instalacion)

        require(ingresosPotenciales > costoShow) { 
            "Crear una nueva función no es redituable en este momento" 
        }
        
        show.agregarFuncion(fecha, hora, instalacion)
    }
}