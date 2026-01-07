package ar.edu.unsam.phm.tpphmgrupo4.domain.node

import ar.edu.unsam.phm.tpphmgrupo4.domain.Entrada
import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode
import java.time.LocalDate

@RelationshipProperties
class EntradaRelacion () {
    @Id
    @GeneratedValue
    var id : String ?= null
    var fechaEntrada: LocalDate = LocalDate.now()
    var showId : String = ""
    @TargetNode
    var show: ShowNodo? = null


    constructor(entrada: Entrada, show: ShowNodo): this(){
        this.fechaEntrada = entrada.fechaEntrada
        this.showId = entrada.showId
        this.show = show

    }

}
