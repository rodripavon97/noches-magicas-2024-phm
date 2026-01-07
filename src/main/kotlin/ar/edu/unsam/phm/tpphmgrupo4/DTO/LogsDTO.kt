package ar.edu.unsam.phm.tpphmgrupo4.DTO

import ar.edu.unsam.phm.tpphmgrupo4.domain.UsuarioComun
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate
import java.time.LocalTime

@Document(collection = "logs")
class LogsDTO {
    lateinit var fecha: LocalDate
    lateinit var hora:LocalTime
    lateinit var nombreAlojamiento: String
    lateinit var usuario:  List<UsuarioDataLogsDTO>
    lateinit var id: String
    lateinit var idShow: String


}