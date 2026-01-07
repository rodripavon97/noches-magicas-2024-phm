package ar.edu.unsam.phm.tpphmgrupo4.Exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Se acabó el stock de entradas")
internal class StockException(message : String) : RuntimeException(message)