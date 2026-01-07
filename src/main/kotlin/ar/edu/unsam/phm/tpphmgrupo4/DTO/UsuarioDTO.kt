package ar.edu.unsam.phm.tpphmgrupo4.DTO

import ar.edu.unsam.phm.tpphmgrupo4.domain.Entrada
import ar.edu.unsam.phm.tpphmgrupo4.domain.UsuarioComun
import java.time.LocalDate
import kotlin.math.roundToInt

class UsuarioDTO {
    var id : Int ?= null
    lateinit var nombre : String
    lateinit var apellido : String
    lateinit var fechaNacimiento : LocalDate
    lateinit var fotoPerfil : String
    lateinit var username : String
    var esAdm : Boolean = false
    var edad : Int ?= null
    var saldo : Double ?= null
    var DNI : Int ?= null
    lateinit var entradasCompradas: List<Entrada>
    lateinit var amigosDelUsuario: List<Int>

    companion object {
        fun fromUsuario( usuarioComun: UsuarioComun) : UsuarioDTO = UsuarioDTO().also {
            it.id = usuarioComun.id.toInt()
            it.nombre = usuarioComun.nombre
            it.apellido = usuarioComun.apellido
            it.fotoPerfil = usuarioComun.fotoPerfil
            it.username = usuarioComun.username
            it.esAdm = usuarioComun.esAdmin
            it.fechaNacimiento = usuarioComun.fechaDeNacimiento
            it.edad = usuarioComun.edad
            it.saldo = usuarioComun.saldo.roundToInt().toDouble()
            it.DNI = usuarioComun.dni
            it.entradasCompradas = usuarioComun.entradasCompradas
            it.amigosDelUsuario = usuarioComun.amigos
        }
    }
}
