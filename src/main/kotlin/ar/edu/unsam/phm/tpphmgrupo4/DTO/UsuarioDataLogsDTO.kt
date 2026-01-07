package ar.edu.unsam.phm.tpphmgrupo4.DTO

import ar.edu.unsam.phm.tpphmgrupo4.domain.UsuarioComun

class UsuarioDataLogsDTO {
    var id : Int ?= null
    lateinit var nombre : String
    lateinit var apellido :String

    companion object{
        fun fromUsuarioDataLogs(usuario : UsuarioComun) : UsuarioDataLogsDTO = UsuarioDataLogsDTO().also {
            it.id = usuario.id.toInt()
            it.nombre = usuario.nombre
            it.apellido = usuario.apellido
        }
    }
}