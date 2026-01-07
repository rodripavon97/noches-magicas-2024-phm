package ar.edu.unsam.phm.tpphmgrupo4.DTO

import ar.edu.unsam.phm.tpphmgrupo4.domain.UsuarioAdmin

class AdminDTO {
    var id : Int ?= null
    lateinit var nombre : String
    lateinit var apellido : String
    lateinit var fotoPerfil : String
    lateinit var username : String
    var esAdm : Boolean = true

    companion object {
        fun fromAdminDTO (admin : UsuarioAdmin): AdminDTO = AdminDTO().also {
            it.id= admin.id.toInt()
            it.nombre = admin.nombre
            it.apellido = admin.apellido
            it.fotoPerfil = admin.fotoPerfil
            it.username = admin.username
            it.esAdm = admin.esAdmin
        }
    }
}