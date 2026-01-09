package ar.edu.unsam.phm.tpphmgrupo4.repositorio.RedisRepository

import ar.edu.unsam.phm.tpphmgrupo4.domain.Carrito
import ar.edu.unsam.phm.tpphmgrupo4.domain.Entrada
import org.springframework.data.repository.CrudRepository

interface RepositorioCarrito:CrudRepository<Carrito, Long>{

    fun getMyCart(idUsuario : Long) : List<Entrada>

    fun findByIdUsuario(idUsuario: Long): Carrito?
}
