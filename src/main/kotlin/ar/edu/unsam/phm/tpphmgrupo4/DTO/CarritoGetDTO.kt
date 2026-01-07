package ar.edu.unsam.phm.tpphmgrupo4.DTO

import ar.edu.unsam.phm.tpphmgrupo4.domain.Ubicacion

class CarritoGetDTO {
    lateinit var idShow: String
    var idFuncion: Int?=null
    var cantidad: Int?=null
    lateinit var ubicacion: Ubicacion
}