package ar.edu.unsam.phm.tpphmgrupo4.DTO

import ar.edu.unsam.phm.tpphmgrupo4.domain.UsuarioComun

class UsuarioDataDTO {
    var id : Int ?= null
    lateinit var nombre : String
    lateinit var apellido : String
    var esAdm : Boolean = false
    lateinit var fotoPerfil : String

    companion object {
        fun fromUserDataPostLogin (user: UsuarioComun): UsuarioDataDTO = UsuarioDataDTO().also {
            it.id = user.id.toInt()
            it.nombre = user.nombre
            it.apellido = user.apellido
            it.esAdm = user.esAdmin
            it.fotoPerfil = user.fotoPerfil
        }
    }
}