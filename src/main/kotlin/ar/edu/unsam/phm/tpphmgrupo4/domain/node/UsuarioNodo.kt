package ar.edu.unsam.phm.tpphmgrupo4.domain.node

import ar.edu.unsam.phm.tpphmgrupo4.domain.Entrada
import ar.edu.unsam.phm.tpphmgrupo4.domain.UsuarioComun
import org.springframework.data.neo4j.core.schema.*

@Node("Usuarios")
class UsuarioNodo() {
    @Id
    @GeneratedValue
    var id : String ?= null

    @Property("Nombre")
    lateinit var nombre: String

    @Property("Apellido")
    lateinit var apellido: String

    @Property("username")
    lateinit var username: String

    @Relationship(type = "SON_AMIGUES", direction = Relationship.Direction.INCOMING)
    var amigues: MutableList<UsuarioNodo> = mutableListOf()

    @Relationship(type = "COMPRO_ENTRADA", direction = Relationship.Direction.OUTGOING)
    var entradasCompradas: MutableList<EntradaRelacion> = mutableListOf()

    fun agregarAmigo(amigo: UsuarioNodo) {
        amigues.add(amigo)
    }

    fun quitarAmigo(amigo: UsuarioNodo) {
        amigues.remove(amigo)
    }

    fun agregarEntrada(showNodo: ShowNodo, entrada : Entrada) {
        val entradaComprada = EntradaRelacion(entrada, showNodo, )
        entradasCompradas.add(entradaComprada)
    }

    constructor(usuario: UsuarioComun) : this() {
        this.nombre = usuario.nombre
        this.apellido = usuario.apellido
        this.username = usuario.username
        this.amigues = usuario.amigues
    }

}