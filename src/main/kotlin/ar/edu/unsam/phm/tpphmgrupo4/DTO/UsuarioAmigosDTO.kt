    package ar.edu.unsam.phm.tpphmgrupo4.DTO

    import ar.edu.unsam.phm.tpphmgrupo4.domain.UsuarioComun

    class UsuarioAmigosDTO {
        var id : Int ?= null
        lateinit var nombre : String
        lateinit var apellido :String
        lateinit var foto : String

        companion object{
            fun fromAmigosDTO( usuario : UsuarioComun) : UsuarioAmigosDTO = UsuarioAmigosDTO().also {
                it.id = usuario.id.toInt()
                it.nombre = usuario.nombre
                it.apellido = usuario.apellido
                it.foto = usuario.fotoPerfil
            }
        }

    }