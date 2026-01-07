package ar.edu.unsam.phm.tpphmgrupo4.domain

import ar.edu.unsam.phm.tpphmgrupo4.Exceptions.SaldoException
import org.springframework.data.redis.core.RedisHash
import jakarta.persistence.Id


@RedisHash("Carrito", timeToLive=18000L)
class Carrito(
    @Id
    val id: Int,
    val items: MutableList<Entrada> = mutableListOf()
){
    fun agregarEntradasACarrito(entradas: List<Entrada>) {
        entradas.forEach { items.add(it) }
    }

    fun limpiarCarrito() {
        items.clear()
    }

    fun totalCarrito(): Double {
        return items.sumOf { it.precioFinal() }
    }

    fun entradasEnCarrito(): Int {
        return items.size
    }

    fun comprarEntradas(usuario: UsuarioComun) {
        if (usuario.saldo >= totalCarrito()) {
            items.forEach { it.vender() }
            usuario.disminuirSaldo(totalCarrito())
            usuario.entradasCompradas.addAll(items)
            limpiarCarrito()
        } else {
            throw SaldoException("El saldo es insuficiente para comprar esta(s) entrada(s)")
        }
    }

}