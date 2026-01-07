package ar.edu.unsam.phm.tpphmgrupo4.Exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Ya existe un comentario en este show")
internal class ComentarioExistenteException(message : String) : RuntimeException(message)

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El show todavía no ocurrió")
internal class ComentarioTempranoException(message : String) : RuntimeException(message)

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "No se puede comentar sin entradas")
internal class ComentarioSinEntradaException(message : String) : RuntimeException(message)