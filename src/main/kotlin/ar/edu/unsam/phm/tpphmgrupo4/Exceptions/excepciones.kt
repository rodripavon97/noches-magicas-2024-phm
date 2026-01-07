package ar.edu.unsam.phm.tpphmgrupo4.Exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

class IdInvalido(mensaje: String) : RuntimeException(mensaje)

class NoExisteElemento(mensaje: String) : RuntimeException(mensaje)

class NoExisteProceso(mensaje: String) : RuntimeException(mensaje)

class ListaDeProcesosVacia(mensaje: String) : RuntimeException(mensaje)

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException(msg: String) : RuntimeException(msg)

@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UnathorizedUser(msg: String) : RuntimeException(msg)


@ResponseStatus(HttpStatus.UNAUTHORIZED)
class UnauthorizedEditData(msg: String) : RuntimeException(msg)

