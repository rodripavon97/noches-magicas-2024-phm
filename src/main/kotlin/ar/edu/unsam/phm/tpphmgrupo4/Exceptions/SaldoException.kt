package ar.edu.unsam.phm.tpphmgrupo4.Exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El saldo es insuficiente")
internal class SaldoException(message: String) : RuntimeException(message)

@ResponseStatus(HttpStatus.UNAUTHORIZED)
internal class SaldoValidationException(msg: String) : RuntimeException(msg)