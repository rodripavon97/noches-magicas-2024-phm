package ar.edu.unsam.phm.tpphmgrupo4.domain.node

import ar.edu.unsam.phm.tpphmgrupo4.domain.Show
import org.springframework.data.neo4j.core.schema.*

@Node("Show")
class ShowNodo() {
    @Id
    @GeneratedValue
    var id : String ?= null
    @Property
    var showId: String = ""
    @Property
    var nombreBanda : String = ""
    @Property
    var nombreRecital : String = ""

    constructor(show: Show): this(){
        this.showId = show.id
        this.nombreBanda = show.nombreBanda
        this.nombreRecital = show.nombreRecital
    }
}